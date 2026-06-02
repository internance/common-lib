package com.internance.common.config;

import com.internance.common.filter.UserContextFilter;
import com.internance.common.web.CurrentUserIdArgumentResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Wires the request-scoped user context for servlet web applications:
 * <ul>
 *   <li>{@link UserContextFilter} binds the {@code X-User-Id} header to
 *       {@link com.internance.common.context.UserContextHolder} — this is what
 *       feeds {@code created_by} / {@code updated_by} auditing
 *       (see {@link JpaAuditingConfig}); without it the auditor is always empty.</li>
 *   <li>{@link CurrentUserIdArgumentResolver} resolves
 *       {@link com.internance.common.web.CurrentUserId @CurrentUserId} controller
 *       parameters.</li>
 * </ul>
 *
 * <p>Auto-configured — applied without component-scanning or {@code @Import}ing
 * this package. Activates only in a servlet web application
 * ({@link ConditionalOnWebApplication}) with Spring MVC on the classpath
 * ({@link ConditionalOnClass}); reactive and non-web modules are unaffected.
 * {@link ConditionalOnMissingBean} lets a consumer override the filter with its
 * own bean of the same type.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({OncePerRequestFilter.class, WebMvcConfigurer.class})
public class UserContextAutoConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public UserContextFilter userContextFilter() {
        return new UserContextFilter();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserIdArgumentResolver());
    }
}
