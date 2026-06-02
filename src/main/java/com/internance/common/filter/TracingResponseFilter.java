package com.internance.common.filter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(Ordered.LOWEST_PRECEDENCE)
public class TracingResponseFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String SPAN_ID_HEADER = "X-Span-Id";

    private final Tracer tracer;

    public TracingResponseFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Span span = tracer.currentSpan();
        if (span != null && !response.isCommitted()) {
            response.setHeader(TRACE_ID_HEADER, span.context().traceId());
            response.setHeader(SPAN_ID_HEADER, span.context().spanId());
        }
        filterChain.doFilter(request, response);
    }
}
