package com.logicaScoolBot.job;

import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.entity.TelegramUser;
import com.logicaScoolBot.repository.StudentRepository;
import com.logicaScoolBot.repository.UserRepository;
import com.logicaScoolBot.service.SbpService;
import com.logicaScoolBot.service.SenderService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QrStatus {

    private final SenderService senderService;
    private final SbpService sbpService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Timed("statusQr")
    @Scheduled(cron = "${cron.job.statusQr}")
    public void statusQr() {
        List<String> list = sbpService.statusQr();
        List<Qr> qrs = sbpService.getAllByQrId(list);

        qrs.forEach(qr -> {
            Student student = studentRepository.findStudentByPhone(qr.getPurpose());
            String textMessage = "Оплата: " + qr.getAmount() + "Р\n" + student.toString();
            userRepository.findAll().stream()
                    .map(TelegramUser::getChatId)
                    .forEach(chatId -> senderService.send(chatId, textMessage));
        });
    }
}
