package com.internance.common.config;

import com.internance.common.filter.TracingResponseFilter;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Registers {@link TracingResponseFilter}, which echoes the current trace/span ids
 * back on the {@code X-Trace-Id} / {@code X-Span-Id} response headers.
 *
 * <p>Auto-configured for servlet web applications only, and only when Micrometer
 * Tracing is on the classpath ({@link ConditionalOnClass}) and a {@link Tracer}
 * bean actually exists ({@link ConditionalOnBean}). Services without tracing are
 * unaffected — no startup failure. {@link ConditionalOnMissingBean} lets a
 * consumer supply its own filter instead.
 *
 * <p>Ordered ({@code afterName}) after the Brave / OpenTelemetry tracing
 * auto-configurations that actually register the {@link Tracer} bean — referenced
 * by name so we neither compile against {@code spring-boot-actuator-autoconfigure}
 * nor fail when a given bridge is absent. Without this the order-sensitive
 * {@link ConditionalOnBean} could be evaluated before the {@link Tracer} exists
 * and the filter would silently back off.
 */
@AutoConfiguration(afterName = {
        "org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration",
        "org.springframework.boot.actuate.autoconfigure.tracing.OpenTelemetryAutoConfiguration"
})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Tracer.class, OncePerRequestFilter.class})
public class TracingAutoConfiguration {

    @Bean
    @ConditionalOnBean(Tracer.class)
    @ConditionalOnMissingBean
    public TracingResponseFilter tracingResponseFilter(Tracer tracer) {
        return new TracingResponseFilter(tracer);
    }
}
