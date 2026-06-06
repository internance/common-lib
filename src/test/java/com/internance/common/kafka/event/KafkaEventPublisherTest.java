package com.internance.common.kafka.event;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @Captor
    ArgumentCaptor<ProducerRecord<String, Object>> recordCaptor;

    KafkaEventPublisher publisher;

    record SamplePayload(String value) {
    }

    record TypedEvent(String value) implements DomainEvent {
        @Override
        public String eventType() {
            return "typed.event.v1";
        }
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        publisher = new KafkaEventPublisher(kafkaTemplate);
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void wrapsPayloadInEnvelopeAndStampsHeaders() {
        SamplePayload payload = new SamplePayload("hello");

        publisher.publish("topic-a", payload);

        ProducerRecord<String, Object> record = capture();
        assertThat(record.topic()).isEqualTo("topic-a");
        assertThat(record.key()).isNull();

        assertThat(record.value()).isInstanceOf(EventEnvelope.class);
        EventEnvelope<?> envelope = (EventEnvelope<?>) record.value();
        assertThat(envelope.eventId()).isNotBlank();
        assertThat(envelope.eventType()).isEqualTo("SamplePayload");
        assertThat(envelope.occurredAt()).isNotNull();
        assertThat(envelope.payload()).isEqualTo(payload);

        assertThat(header(record, EventKafkaHeaders.EVENT_ID)).isEqualTo(envelope.eventId());
        assertThat(header(record, EventKafkaHeaders.EVENT_TYPE)).isEqualTo("SamplePayload");
    }

    @Test
    void usesProvidedKeyForPartitioning() {
        publisher.publish("topic-a", "partition-key", new SamplePayload("hello"));

        assertThat(capture().key()).isEqualTo("partition-key");
    }

    @Test
    void derivesEventTypeFromDomainEvent() {
        publisher.publish("topic-a", new TypedEvent("x"));

        ProducerRecord<String, Object> record = capture();
        assertThat(((EventEnvelope<?>) record.value()).eventType()).isEqualTo("typed.event.v1");
        assertThat(header(record, EventKafkaHeaders.EVENT_TYPE)).isEqualTo("typed.event.v1");
    }

    @Test
    void explicitEventTypeOverridesPayloadClassName() {
        publisher.publish("topic-a", "k", "custom.type", new SamplePayload("hello"));

        ProducerRecord<String, Object> record = capture();
        assertThat(((EventEnvelope<?>) record.value()).eventType()).isEqualTo("custom.type");
        assertThat(header(record, EventKafkaHeaders.EVENT_TYPE)).isEqualTo("custom.type");
    }

    private ProducerRecord<String, Object> capture() {
        org.mockito.Mockito.verify(kafkaTemplate).send(recordCaptor.capture());
        return recordCaptor.getValue();
    }

    private static String header(ProducerRecord<String, Object> record, String key) {
        Header header = record.headers().lastHeader(key);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }
}
