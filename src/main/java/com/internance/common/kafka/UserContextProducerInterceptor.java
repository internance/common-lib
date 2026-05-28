package com.internance.common.kafka;

import com.internance.common.context.UserContextHolder;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserContextProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {

    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
        UserContextHolder.getUserId().ifPresent(id -> {
            Headers headers = record.headers();
            if (headers.lastHeader(UserContextKafkaHeaders.USER_ID) == null) {
                headers.add(UserContextKafkaHeaders.USER_ID,
                    id.toString().getBytes(StandardCharsets.UTF_8));
            }
        });
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
