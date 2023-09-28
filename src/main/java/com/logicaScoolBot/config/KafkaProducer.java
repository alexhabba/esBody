
package com.logicaScoolBot.config;

import com.logicaScoolBot.dto.kafka.KafkaEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * Продюсер для отправки сообщения в топик.
 */
@Slf4j
@Getter
@Component
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaProducer {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public KafkaProducer(KafkaProperties properties) {
        this.kafkaTemplate = new KafkaTemplate<>(getProducerFactory(properties));
    }

    private ProducerFactory<String, KafkaEvent> getProducerFactory(KafkaProperties properties) {
        return new DefaultKafkaProducerFactory<>(getProducerConfigs(properties));
    }

    private Map<String, Object> getProducerConfigs(KafkaProperties properties) {
        return new HashMap<>(properties.buildProducerProperties());
    }

    public void send(KafkaEvent dto, String topicName) {
        ListenableFuture<SendResult<String, KafkaEvent>> future = kafkaTemplate.send(topicName, dto);
        future.addCallback(new ProcessMessageCallback(dto));
    }
}
