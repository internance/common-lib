package com.internance.common.kafka.event;

/**
 * Kafka header keys stamped onto every record published through
 * {@link EventPublisher}, so consumers can route or filter without deserializing
 * the value. They mirror the matching fields of {@link EventEnvelope}.
 */
public final class EventKafkaHeaders {

    public static final String EVENT_ID = "x-event-id";

    public static final String EVENT_TYPE = "x-event-type";

    private EventKafkaHeaders() {
    }
}
