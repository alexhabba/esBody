package com.logicaScoolBot.service;

import com.logicaScoolBot.repository.QrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrRepository qrRepository;

    @Scheduled(cron = "${cron.job.delete_qr}")
    @Transactional
    public void deleteQr() {
        System.out.println("start delete Qr");
        qrRepository.deleteQr(LocalDateTime.now().minusDays(4));
        System.out.println("end delete Qr");
    }
}

