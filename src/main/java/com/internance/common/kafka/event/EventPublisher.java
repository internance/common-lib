package com.internance.common.kafka.event;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Service-facing abstraction for publishing domain events to Kafka. Wraps the
 * payload in a standard {@link EventEnvelope}, stamps the {@code x-event-id} /
 * {@code x-event-type} headers, and sends it through the application's
 * {@code KafkaTemplate}.
 *
 * <p>Auto-wired by {@code CommonKafkaPublisherAutoConfiguration} whenever a
 * {@code KafkaTemplate} bean exists, so any service depending on common-lib can
 * simply inject {@code EventPublisher}. User-context propagation is automatic —
 * the producer interceptor wired by {@code CommonKafkaAutoConfiguration} stamps
 * the {@code x-user-id} header on every outgoing record.
 *
 * <p>The returned {@link CompletableFuture} completes when the broker
 * acknowledges the send; ignore it for fire-and-forget, or chain on it to react
 * to delivery success/failure.
 */
public interface EventPublisher {

    /**
     * Publishes {@code payload} to {@code topic} with no partition key. The event
     * type is derived from the payload ({@link DomainEvent#eventType()} when
     * implemented, otherwise the simple class name).
     */
    CompletableFuture<SendResult<String, Object>> publish(String topic, Object payload);

    /**
     * Publishes {@code payload} to {@code topic} using {@code key} for
     * partitioning (records with the same key keep their relative order). The
     * event type is derived from the payload.
     */
    CompletableFuture<SendResult<String, Object>> publish(String topic, String key, Object payload);

    /**
     * Publishes {@code payload} to {@code topic} with an explicit {@code eventType}
     * and partition {@code key}. Use this overload when the type name should not
     * be tied to the payload class.
     *
     * @param key may be {@code null} for no partition key
     */
    CompletableFuture<SendResult<String, Object>> publish(String topic, String key, String eventType, Object payload);
}
