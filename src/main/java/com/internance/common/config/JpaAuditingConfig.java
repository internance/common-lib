package com.internance.common.config;

import com.internance.common.context.UserContextHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.UUID;

/**
 * Enables Spring Data JPA auditing so that {@code created_by} / {@code updated_by}
 * (declared on {@link com.internance.common.entity.BaseEntity}) are populated
 * automatically from the current request's {@link UserContextHolder}.
 *
 * <p>The auditor is the authenticated user's {@link UUID} (a UUID v7 issued by
 * {@code IdGenerator}). When no user context is bound — system jobs, async work,
 * unauthenticated calls — the auditor resolves to {@code Optional.empty()} and the
 * columns are left {@code null}.
 *
 * <p>This is a Spring Boot {@link AutoConfiguration auto-configuration}: it is
 * registered via {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}
 * and applied automatically to any consuming application, without requiring it to
 * component-scan or {@code @Import} this package. It activates only when Spring
 * Data JPA is on the classpath ({@link ConditionalOnClass}), so non-JPA modules
 * are unaffected.
 *
 * <p><strong>Do not declare {@code @EnableJpaAuditing} again in the consuming
 * application;</strong> Spring rejects more than one auditing configuration. If
 * the app already enables auditing, exclude this auto-configuration (e.g. via
 * {@code spring.autoconfigure.exclude}) and reuse the {@link #auditorAware()}
 * bean on the app's own auditing setup instead.
 */
@AutoConfiguration
@ConditionalOnClass(AuditingEntityListener.class)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return UserContextHolder::getUserId;
    }
}
