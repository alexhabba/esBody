package com.logicaScoolBot.job;

import com.logicaScoolBot.entity.Client;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.entity.TelegramUser;
import com.logicaScoolBot.repository.ClientRepository;
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
    private final ClientRepository clientRepository;

    @Timed("statusQr")
    @Scheduled(cron = "${cron.job.statusQr}")
    public void statusQr() {
        List<String> list = sbpService.statusQr();
        List<Qr> qrs = sbpService.getAllByQrId(list);

        qrs.forEach(qr -> {
            Client client = clientRepository.findStudentByPhone(qr.getPurpose());
            String textMessage = "Оплата: " + qr.getAmount() + "Р\n" + client.toString();
            userRepository.findAll().stream()
                    .map(TelegramUser::getChatId)
                    .forEach(chatId -> {
                        try {
                            senderService.send(chatId, textMessage);
                        } catch (Exception ignore) {
                        }
                    });
        });
    }
}
