package com.logicaScoolBot.service;

import com.logicaScoolBot.dto.kafka.KafkaEvent;

public interface KafkaSenderService {

    void send(KafkaEvent event, String topic);
}
