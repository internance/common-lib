package com.internance.common.kafka.event;

import com.internance.common.utils.IdGenerator;

import java.time.Instant;
import java.util.Objects;

/**
 * Standard message envelope every event published through {@link EventPublisher}
 * is wrapped in, so consuming services share one contract regardless of payload.
 *
 * <ul>
 *   <li>{@code eventId} — globally unique, time-ordered id ({@link IdGenerator});
 *       use it for idempotent consumption / de-duplication;</li>
 *   <li>{@code eventType} — logical type name used for routing/filtering, also
 *       mirrored onto the {@code x-event-type} Kafka header;</li>
 *   <li>{@code occurredAt} — when the event was created by the producer;</li>
 *   <li>{@code payload} — the domain-specific body.</li>
 * </ul>
 *
 * <p>A consumer reads it back by declaring the concrete type on the listener
 * method, e.g. {@code void on(EventEnvelope<UserCreated> event)} — Spring Kafka
 * resolves the generic from the method signature when deserializing.
 */
public record EventEnvelope<T>(
        String eventId,
        String eventType,
        Instant occurredAt,
        T payload
) {

    public EventEnvelope {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }

    /**
     * Creates an envelope with a freshly generated {@code eventId} and the current
     * instant as {@code occurredAt}.
     */
    public static <T> EventEnvelope<T> of(String eventType, T payload) {
        return new EventEnvelope<>(IdGenerator.generateString(), eventType, Instant.now(), payload);
    }
}
