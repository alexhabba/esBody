package com.logicaScoolBot.service;

import com.logicaScoolBot.entity.Consumption;
import com.logicaScoolBot.entity.TelegramUser;
import com.logicaScoolBot.enums.Role;
import com.logicaScoolBot.exception.HandlerMessageException;
import com.logicaScoolBot.repository.ConsumptionRepository;
import com.logicaScoolBot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements ConsumptionService, HandlerMessage {

    private final ConsumptionRepository repository;
    private final UserRepository userRepository;

    private final SenderService senderService;

    @Override
    public Consumption save(Consumption consumption) {
        return repository.save(consumption);
    }

    @Override
    public int getAmountMonth(LocalDateTime dateTime) {
        return repository.getAmountMonth(dateTime);
    }

    @Override
    @SneakyThrows
    public void handle(Update update) {
        addConsumption(update);
    }

    public void addConsumption(Update update) {
        List<Long> chatIdSuperUsers = userRepository.findAllByRole(Role.SUPER_ADMIN).stream()
                .map(TelegramUser::getChatId)
                .collect(Collectors.toList());
        if (chatIdSuperUsers.contains(update.getMessage().getChatId())) {
            List<String> list = Arrays.asList(update.getMessage().getText().split("\\s+"));

            long amount = 0;

            try {
                amount = Long.parseLong(list.get(0));


                Consumption build = Consumption.builder()
                        .amount(amount)
                        .description(getDescription(update.getMessage().getText(), list, amount))
                        .build();

                save(build);
                senderService.send(update.getMessage().getChatId(), "Расход добавлен");
                Long chatId2 = chatIdSuperUsers.stream()
                        .filter(c -> !c.equals(update.getMessage().getChatId()))
                        .findFirst().get();
                senderService.send(chatId2, "Добавлен расход\n" + update.getMessage().getText());
                //todo
                throw new HandlerMessageException();
            } catch (NumberFormatException ex) {

            }
        }
    }

    @NotNull
    private String getDescription(String messageText, List<String> list, long amount) {
        return messageText
                .replace(Long.toString(amount), "")
                .replace(list.stream()
                        .findAny().get(), "")
                .trim();
    }
}
