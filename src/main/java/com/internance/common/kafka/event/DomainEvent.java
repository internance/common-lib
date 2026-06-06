package com.internance.common.kafka.event;

/**
 * Optional marker for event payloads that want to declare their own logical type
 * name. When a payload passed to {@link EventPublisher} implements this interface
 * its {@link #eventType()} is used as the envelope's {@code eventType}; otherwise
 * the publisher falls back to the payload's simple class name.
 *
 * <p>Implementing it is never required — any object can be published — it only
 * gives a stable, refactor-proof type name independent of the Java class name.
 */
public interface DomainEvent {

    /**
     * Logical event type name. Defaults to the simple class name; override to keep
     * the wire contract stable across class renames.
     */
    default String eventType() {
        return getClass().getSimpleName();
    }
}
