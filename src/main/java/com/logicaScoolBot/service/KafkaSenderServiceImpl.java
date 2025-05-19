package com.logicaScoolBot.service;

import com.logicaScoolBot.config.KafkaProducer;
import com.logicaScoolBot.dto.kafka.KafkaEvent;
import com.logicaScoolBot.entity.Client;
import com.logicaScoolBot.entity.Consumption;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.mapper.ConsumptionMapper;
import com.logicaScoolBot.mapper.QrMapper;
import com.logicaScoolBot.mapper.ClientMapper;
import com.logicaScoolBot.repository.ConsumptionRepository;
import com.logicaScoolBot.repository.QrRepository;
import com.logicaScoolBot.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {

    private final KafkaProducer kafkaProducer;

    private final ClientRepository clientRepository;
    private final QrRepository qrRepository;
    private final ConsumptionRepository consumptionRepository;

    private final ClientMapper studentMapper;
    private final QrMapper qrMapper;
    private final ConsumptionMapper consumptionMapper;

    @Override
    public void send(KafkaEvent event, String topic) {
        kafkaProducer.send(event, topic);
    }

//    @Scheduled(cron = "${cron.job.replica}")
    public void taskReplication() {

        replicationStudent();

        replicationQr();

        replicationConsumption();
    }

    private void replicationStudent() {
        List<Client> clients = clientRepository.findAllByNotSend();
        clients.stream()
                .map(studentMapper::toDto)
                .forEach(s -> send(s, "topStudent"));
        clients.forEach(s -> {
            s.setSend(true);
        });
        clientRepository.saveAllAndFlush(clients);
    }

    private void replicationQr() {
        List<Qr> qrc = qrRepository.findAllByNotSend();
        qrc.stream()
                .map(qrMapper::toDto)
                .forEach(qr -> send(qr, "topQr"));
        qrc.forEach(s -> {
            s.setSend(true);
        });
        qrRepository.saveAllAndFlush(qrc);
    }


    private void replicationConsumption() {
        List<Consumption> consumptions = consumptionRepository.findAllByNotSend();
        consumptions.stream()
                .map(consumptionMapper::toDto)
                .forEach(s -> send(s, "topConsumption"));
        consumptions.forEach(s -> {
            s.setSend(true);
        });
        consumptionRepository.saveAllAndFlush(consumptions);
    }

}
