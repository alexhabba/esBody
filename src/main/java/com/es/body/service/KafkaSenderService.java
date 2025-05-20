package com.es.body.service;

import com.es.body.dto.kafka.KafkaEvent;

public interface KafkaSenderService {

    void send(KafkaEvent event, String topic);
}
