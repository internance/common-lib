package com.internance.common.config;

import com.internance.common.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Registers {@link GlobalExceptionHandler}, the {@code @RestControllerAdvice} that
 * maps {@code BusinessException}, validation errors and uncaught exceptions to the
 * standard {@code ApiResponse} envelope.
 *
 * <p>Auto-configured for servlet web applications only ({@link ConditionalOnWebApplication},
 * {@link ConditionalOnClass}). {@link ConditionalOnMissingBean} means a service that
 * declares its own {@code GlobalExceptionHandler} bean replaces this one, so the
 * common handler quietly yields rather than conflicting.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(RestControllerAdvice.class)
public class ApiExceptionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
