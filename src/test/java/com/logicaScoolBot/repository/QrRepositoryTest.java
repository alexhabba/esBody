package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Client;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.enums.QrStatus;
import com.logicaScoolBot.service.QrServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class QrRepositoryTest {

    @Autowired
    private QrRepository qrRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private QrServiceImpl qrService;

    @Test
    void updateStatuses() throws InterruptedException {
//        Student student = createStudent();
//        Qr qr = createQr();
//
//        student.setQrc(List.of(qr));
//        qr.setStudent(student);

//        Student save = studentRepository.save(student);
//        List<String> collect = save.getQrc().stream()
//                .map(Qr::getQrcId)
//                .collect(Collectors.toList());

//        Thread.sleep(2000);
//        Thread.sleep(2000);
//        Thread.sleep(2000);
//        qrRepository.updateStatuses(List.of("sfgdfgdfg"));
//        qrService.updateQrStatuses(List.of("sfgdfgdfg"), LocalDateTime.now());

//        Integer amountSumToDay = qrRepository.getAmountSumToDay(LocalDateTime.now());
    }

    private Client createStudent() {
        return Client.builder()
                .id(1L)
                .city("wfrsd")
                .phone("89098766666")
                .fullName("sdfsdf")
                .build();
    }

    private Qr createQr() {
        return Qr.builder()
                .status(QrStatus.NotStarted)
                .qrcId("sfgdfgdfg")
                .amount(1L)
                .purpose("sfgsgfd")
                .build();
    }

}