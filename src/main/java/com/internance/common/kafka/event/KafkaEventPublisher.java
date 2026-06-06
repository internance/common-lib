package com.internance.common.kafka.event;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * {@link EventPublisher} backed by a Spring {@link KafkaTemplate}. Builds the
 * {@link EventEnvelope}, attaches the routing headers, and delegates the actual
 * send to the template.
 *
 * <p>Serialization of the envelope is the template's concern: configure a JSON
 * value serializer on the producer (see the project README) so envelopes are
 * written as JSON.
 */
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate, "kafkaTemplate must not be null");
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> publish(String topic, Object payload) {
        return publish(topic, null, resolveEventType(payload), payload);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> publish(String topic, String key, Object payload) {
        return publish(topic, key, resolveEventType(payload), payload);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> publish(String topic, String key, String eventType, Object payload) {
        EventEnvelope<Object> envelope = EventEnvelope.of(eventType, payload);
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, null, key, envelope);
        record.headers()
                .add(new RecordHeader(EventKafkaHeaders.EVENT_ID,
                        envelope.eventId().getBytes(StandardCharsets.UTF_8)))
                .add(new RecordHeader(EventKafkaHeaders.EVENT_TYPE,
                        envelope.eventType().getBytes(StandardCharsets.UTF_8)));
        if (log.isDebugEnabled()) {
            log.debug("Publishing event id={} type={} topic={} key={}",
                    envelope.eventId(), envelope.eventType(), topic, key);
        }
        return kafkaTemplate.send(record);
    }

    private static String resolveEventType(Object payload) {
        Objects.requireNonNull(payload, "payload must not be null");
        if (payload instanceof DomainEvent event) {
            return event.eventType();
        }
        return payload.getClass().getSimpleName();
    }
}
