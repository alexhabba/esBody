package com.es.body.service;

import com.es.body.entity.TelegramUser;
import com.es.body.enums.Role;
import com.es.body.repository.ClientRepository;
import com.es.body.repository.UserRepository;
import com.es.body.entity.Client;
import com.es.body.exception.HandlerMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;

import static com.es.body.bot.TelegramBot.YOU_HAVE_NOT_RIGHTS;
import static com.es.body.constnt.Constant.ADD_NEW_CLIENT;
import static com.es.body.utils.PhoneUtils.getPhoneFormat;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService, HandlerMessage {

    private final SenderService senderService;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @Override
    public List<Client> getAllClient() {
        return clientRepository.findAll();
    }

    @Override
    public void handle(Update update) {
        String message = update.getMessage().getText();
        if (message.startsWith(ADD_NEW_CLIENT) && message.length() > ADD_NEW_CLIENT.length()) {
            String[] split = message.split("\n");
            String phone = getPhoneFormat(split[2]);
            if (phone.length() != 10) {
                senderService.send(update.getMessage().getChatId(), "Неверный формат телефона");
                throw new HandlerMessageException();
            }
            Client clientByPhone = clientRepository.findStudentByPhone(phone);
            if (clientByPhone != null) {
                senderService.send(update.getMessage().getChatId(), "Такой клиент уже есть в базе");
                throw new HandlerMessageException();
            }
            TelegramUser telegramUser = userRepository.findById(update.getMessage().getChatId()).orElseThrow();
            if (telegramUser.getRole() == Role.MANAGER || telegramUser.getRole() == Role.SUPER_ADMIN) {
                Client client = Client.builder()
                        .id(clientRepository.findAll().stream()
                                .map(Client::getId)
                                .max(Comparator.naturalOrder())
                                .orElse(0L) + 1)
                        .fullName(split[1].trim())
                        .phone(phone.trim())
                        .nameAdder(telegramUser.getFirstName())
                        .build();
                clientRepository.save(client);
                senderService.send(update.getMessage().getChatId(), "Клиент добавлен.");
            } else {
                senderService.send(telegramUser.getChatId(), YOU_HAVE_NOT_RIGHTS);
            }
        } else if (message.equals(ADD_NEW_CLIENT)) {
            senderService.send(update.getMessage().getChatId(), "Для добавления нового клиента," +
                    " необходимо отправить сообщение по шаблону:\n\n" +
                    ADD_NEW_CLIENT + "\n" +
                    "Имя фамилия клиента\n" +
                    "телефон без 8 и слитно");
        }
    }


}
