package com.internance.common.kafka;

import com.internance.common.context.UserContext;
import com.internance.common.context.UserContextHolder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.RecordInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserContextRecordInterceptor<K, V> implements RecordInterceptor<K, V> {

    private static final Logger log = LoggerFactory.getLogger(UserContextRecordInterceptor.class);

    @Override
    public ConsumerRecord<K, V> intercept(ConsumerRecord<K, V> record, Consumer<K, V> consumer) {
        Header header = record.headers().lastHeader(UserContextKafkaHeaders.USER_ID);
        if (header == null) {
            UserContextHolder.clear();
            return record;
        }
        String raw = new String(header.value(), StandardCharsets.UTF_8);
        try {
            UserContextHolder.set(new UserContext(UUID.fromString(raw)));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid {} kafka header value: {}", UserContextKafkaHeaders.USER_ID, raw);
            UserContextHolder.clear();
        }
        return record;
    }

    @Override
    public void afterRecord(ConsumerRecord<K, V> record, Consumer<K, V> consumer) {
        UserContextHolder.clear();
    }
}
