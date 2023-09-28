package com.logicaScoolBot.service;

import com.logicaScoolBot.config.KafkaProducer;
import com.logicaScoolBot.dto.kafka.KafkaEvent;
import com.logicaScoolBot.entity.AdministratorWorkDay;
import com.logicaScoolBot.entity.Consumption;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.mapper.AdministratorWorkDayMapper;
import com.logicaScoolBot.mapper.ConsumptionMapper;
import com.logicaScoolBot.mapper.QrMapper;
import com.logicaScoolBot.mapper.StudentMapper;
import com.logicaScoolBot.repository.AdministratorWorkDayRepository;
import com.logicaScoolBot.repository.ConsumptionRepository;
import com.logicaScoolBot.repository.QrRepository;
import com.logicaScoolBot.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaSenderServiceImpl implements KafkaSenderService {

    private final KafkaProducer kafkaProducer;

    private final StudentRepository studentRepository;
    private final QrRepository qrRepository;
    private final ConsumptionRepository consumptionRepository;
    private final AdministratorWorkDayRepository administratorWorkDayRepository;

    private final StudentMapper studentMapper;
    private final QrMapper qrMapper;
    private final AdministratorWorkDayMapper administratorWorkDayMapper;
    private final ConsumptionMapper consumptionMapper;

    @Override
    public void send(KafkaEvent event) {
        kafkaProducer.send(event, "topic_test");
    }

    @Scheduled(cron = "${cron.job.replica}")
    public void taskReplication() {

        replicationStudent();

        replicationQr();

        replicationAdministratorWorkDay();

        replicationConsumption();
    }

    private void replicationStudent() {
        List<Student> students = studentRepository.findAllByNotSend();
        students.stream()
                .map(studentMapper::toDto)
                .forEach(this::send);
        students.forEach(s -> {
            s.setSend(true);
        });
        studentRepository.saveAllAndFlush(students);
    }

    private void replicationQr() {
        List<Qr> qrc = qrRepository.findAllByNotSend();
        qrc.stream()
                .map(qrMapper::toDto)
                .forEach(this::send);
        qrc.forEach(s -> {
            s.setSend(true);
        });
        qrRepository.saveAllAndFlush(qrc);
    }

    private void replicationAdministratorWorkDay() {
        List<AdministratorWorkDay> administratorWorkDays = administratorWorkDayRepository.findAllByNotSend();
        administratorWorkDays.stream()
                .map(administratorWorkDayMapper::toDto)
                .forEach(this::send);
        administratorWorkDays.forEach(s -> {
            s.setSend(true);
        });
        administratorWorkDayRepository.saveAllAndFlush(administratorWorkDays);
    }

    private void replicationConsumption() {
        List<Consumption> consumptions = consumptionRepository.findAllByNotSend();
        consumptions.stream()
                .map(consumptionMapper::toDto)
                .forEach(this::send);
        consumptions.forEach(s -> {
            s.setSend(true);
        });
        consumptionRepository.saveAllAndFlush(consumptions);
    }

}
