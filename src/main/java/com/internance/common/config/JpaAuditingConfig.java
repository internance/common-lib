package com.internance.common.config;

import com.internance.common.context.UserContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
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
 * <p><strong>Do not declare {@code @EnableJpaAuditing} again in the consuming
 * application;</strong> Spring rejects more than one auditing configuration. If
 * the app already enables auditing, drop this config from the component scan and
 * register the {@link #auditorAware()} bean on the app's own auditing setup
 * instead.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return UserContextHolder::getUserId;
    }
}
