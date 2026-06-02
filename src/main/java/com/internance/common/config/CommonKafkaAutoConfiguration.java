package com.internance.common.config;

import com.internance.common.kafka.UserContextProducerInterceptor;
import com.internance.common.kafka.UserContextRecordInterceptor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.RecordInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Propagates the user context across Kafka:
 * <ul>
 *   <li>a {@link RecordInterceptor} ({@link UserContextRecordInterceptor}) binds the
 *       {@code x-user-id} header to the context while a record is consumed — Spring
 *       Boot wires a single such bean into its listener container factory;</li>
 *   <li>a {@link DefaultKafkaProducerFactoryCustomizer} appends
 *       {@link UserContextProducerInterceptor} to the producer's
 *       {@code interceptor.classes} so the header is stamped on outgoing records.</li>
 * </ul>
 *
 * <p>Auto-configured only when Spring Kafka is on the classpath
 * ({@link ConditionalOnClass}), so non-Kafka services never fail to start. The
 * producer customizer appends to (rather than replaces) any interceptors the
 * service already configured. {@link ConditionalOnMissingBean} lets a consumer
 * override either bean.
 */
@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
public class CommonKafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RecordInterceptor<Object, Object> userContextRecordInterceptor() {
        return new UserContextRecordInterceptor<>();
    }

    @Bean
    @ConditionalOnMissingBean(name = "userContextKafkaProducerCustomizer")
    public DefaultKafkaProducerFactoryCustomizer userContextKafkaProducerCustomizer() {
        return factory -> {
            String interceptor = UserContextProducerInterceptor.class.getName();
            List<String> classes = readInterceptorClasses(
                    factory.getConfigurationProperties().get(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG));
            if (!classes.contains(interceptor)) {
                classes.add(interceptor);
                factory.updateConfigs(Map.of(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, classes));
            }
        };
    }

    private static List<String> readInterceptorClasses(Object configured) {
        List<String> classes = new ArrayList<>();
        if (configured instanceof String s) {
            for (String entry : s.split(",")) {
                String trimmed = entry.trim();
                if (!trimmed.isEmpty()) {
                    classes.add(trimmed);
                }
            }
        } else if (configured instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof Class<?> clazz) {
                    classes.add(clazz.getName());
                } else if (entry != null) {
                    classes.add(String.valueOf(entry));
                }
            }
        }
        return classes;
    }
}
