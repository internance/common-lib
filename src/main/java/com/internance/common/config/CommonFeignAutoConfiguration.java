package com.internance.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internance.common.feign.ApiResponseFeignErrorDecoder;
import com.internance.common.feign.UserContextFeignInterceptor;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Registers the OpenFeign integration beans:
 * <ul>
 *   <li>{@link UserContextFeignInterceptor} — propagates the {@code X-User-Id}
 *       header to downstream services, so their {@code created_by} auditing sees
 *       the original caller.</li>
 *   <li>{@link ApiResponseFeignErrorDecoder} — turns a downstream
 *       {@code ApiResponse} error envelope back into a {@code BusinessException}.</li>
 * </ul>
 *
 * <p>Auto-configured only when Feign is on the classpath ({@link ConditionalOnClass}),
 * so non-Feign services are unaffected. The error decoder additionally requires an
 * {@link ObjectMapper} bean ({@link ConditionalOnBean}). {@link ConditionalOnMissingBean}
 * lets a consumer override either bean with its own.
 */
@AutoConfiguration
@ConditionalOnClass(RequestInterceptor.class)
public class CommonFeignAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserContextFeignInterceptor userContextFeignInterceptor() {
        return new UserContextFeignInterceptor();
    }

    @Bean
    @ConditionalOnBean(ObjectMapper.class)
    @ConditionalOnMissingBean(ErrorDecoder.class)
    public ErrorDecoder apiResponseFeignErrorDecoder(ObjectMapper objectMapper) {
        return new ApiResponseFeignErrorDecoder(objectMapper);
    }
}
