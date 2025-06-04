package com.es.body.job;

import com.es.body.entity.Client;
import com.es.body.entity.Qr;
import com.es.body.entity.TelegramUser;
import com.es.body.repository.ClientRepository;
import com.es.body.repository.UserRepository;
import com.es.body.service.SbpService;
import com.es.body.service.SenderService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.es.body.enums.Role.MANAGER;

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
            userRepository.findAllByRoles(List.of(MANAGER)).stream()
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
