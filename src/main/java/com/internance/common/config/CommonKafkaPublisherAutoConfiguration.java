package com.internance.common.config;

import com.internance.common.kafka.event.EventPublisher;
import com.internance.common.kafka.event.KafkaEventPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Exposes a ready-to-inject {@link EventPublisher} so any service depending on
 * common-lib can publish domain events without re-implementing the envelope /
 * header boilerplate.
 *
 * <p>Auto-configured only when Spring Kafka is on the classpath
 * ({@link ConditionalOnClass}) and exactly one {@link KafkaTemplate} bean is
 * resolvable ({@link ConditionalOnSingleCandidate}) — non-Kafka services are
 * unaffected, and apps with several templates back off gracefully rather than
 * failing on an ambiguous injection. {@link ConditionalOnMissingBean} lets a
 * consumer supply its own publisher.
 *
 * <p>The template is injected as {@code KafkaTemplate<?, ?>} so <em>any</em>
 * generic signature matches — a service does not have to declare exactly
 * {@code KafkaTemplate<String, Object>} to opt in, and one declared with a
 * different value type no longer causes an opaque startup failure. The publisher
 * writes {@code <String, Object>} records; serialization of the (Object) envelope
 * is the application's configured value serializer's concern (see the README).
 *
 * <p>Ordered ({@code afterName}) after Spring Boot's
 * {@code KafkaAutoConfiguration} that registers the {@link KafkaTemplate} bean —
 * referenced by name so we don't compile against it. Without this the
 * order-sensitive condition could be evaluated before the template exists and the
 * publisher would silently back off.
 */
@AutoConfiguration(afterName = "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration")
@ConditionalOnClass(KafkaTemplate.class)
public class CommonKafkaPublisherAutoConfiguration {

    @Bean
    @ConditionalOnSingleCandidate(KafkaTemplate.class)
    @ConditionalOnMissingBean(EventPublisher.class)
    @SuppressWarnings("unchecked")
    public EventPublisher eventPublisher(KafkaTemplate<?, ?> kafkaTemplate) {
        return new KafkaEventPublisher((KafkaTemplate<String, Object>) kafkaTemplate);
    }
}
