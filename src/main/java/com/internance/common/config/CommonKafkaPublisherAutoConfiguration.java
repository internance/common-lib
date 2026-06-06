package com.internance.common.config;

import com.internance.common.kafka.event.EventPublisher;
import com.internance.common.kafka.event.KafkaEventPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Exposes a ready-to-inject {@link EventPublisher} so any service depending on
 * common-lib can publish domain events without re-implementing the envelope /
 * header boilerplate.
 *
 * <p>Auto-configured only when Spring Kafka is on the classpath
 * ({@link ConditionalOnClass}) and a {@link KafkaTemplate} bean actually exists
 * ({@link ConditionalOnBean}) — non-Kafka services are unaffected.
 * {@link ConditionalOnMissingBean} lets a consumer supply its own publisher.
 *
 * <p>Ordered ({@code afterName}) after Spring Boot's
 * {@code KafkaAutoConfiguration} that registers the {@link KafkaTemplate} bean —
 * referenced by name so we don't compile against it. Without this the
 * order-sensitive {@link ConditionalOnBean} could be evaluated before the
 * template exists and the publisher would silently back off.
 */
@AutoConfiguration(afterName = "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration")
@ConditionalOnClass(KafkaTemplate.class)
public class CommonKafkaPublisherAutoConfiguration {

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    @ConditionalOnMissingBean(EventPublisher.class)
    public EventPublisher eventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaEventPublisher(kafkaTemplate);
    }
}
