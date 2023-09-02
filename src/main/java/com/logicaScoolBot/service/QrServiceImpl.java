package com.logicaScoolBot.service;

import com.logicaScoolBot.repository.QrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrRepository qrRepository;

    @Scheduled(cron = "${cron.job.delete_qr}")
    @Transactional
    public void deleteQr() {
        qrRepository.deleteQr(LocalDateTime.now().minusDays(4));
    }

    @Override
    @Transactional
    public void updateQrStatuses(List<String> qrsIdList, LocalDateTime dateTime ) {
        qrRepository.updateStatuses(qrsIdList, dateTime);
    }
}

