package com.es.body.service;

import com.es.body.exception.HandlerMessageException;
import com.es.body.repository.ConsumptionRepository;
import com.es.body.repository.UserRepository;
import com.es.body.entity.Consumption;
import com.es.body.entity.TelegramUser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.es.body.enums.Role.SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements ConsumptionService, HandlerMessage {

    private final ConsumptionRepository repository;
    private final UserRepository userRepository;

    private final SenderService senderService;

    @Override
    public void save(Consumption consumption) {
        repository.save(consumption);
    }

    @Override
    public void saveAll(List<Consumption> consumptions) {
        repository.saveAll(consumptions);
    }

    @Override
    public int getAmountMonth(LocalDateTime dateTime) {
        return repository.getAmountMonth(dateTime);
    }

    @Override
    public Set<String> findMissingPaymentIds(Set<String> paymentIds) {
        Set<String> found = repository.findAllByPaymentIds(paymentIds);
        Set<String> missing = new HashSet<>(paymentIds);
        missing.removeAll(found); // оставим только отсутствующие
        return missing;
    }


    @Override
    @SneakyThrows
    public void handle(Update update) {
        addConsumption(update);
    }

    public void addConsumption(Update update) {
        List<Long> chatIdSuperUsers = userRepository.findAllByRoles(List.of(SUPER_ADMIN)).stream()
                .map(TelegramUser::getChatId)
                .collect(Collectors.toList());
        if (chatIdSuperUsers.contains(update.getMessage().getChatId())) {
            List<String> list = Arrays.asList(update.getMessage().getText().split("\\s+"));

            long amount = 0;

            try {
                amount = Long.parseLong(list.get(0));


                Consumption build = Consumption.builder()
                        .amount(BigDecimal.valueOf(amount))
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

    @Override
    public List<Consumption> getConsumptionByDateTime(LocalDateTime dateTime) {
        List<Consumption> consumptionByDateTime = repository.getConsumptionByDateTime(dateTime);

        // получаем множество PaymentId где кол-во больше 1 и исключаем из отчета
        Set<String> paymentIdsWhereCountMoreOne = consumptionByDateTime.stream()
                .collect(Collectors.groupingBy(
                        Consumption::getTransactionId,
                        Collectors.counting()
                ))
                .entrySet().stream()  // Преобразуем Map в Stream<Map.Entry>
                .filter(entry -> entry.getValue() > 1)  // Оставляем только count > 1
                .collect(Collectors.toMap(
                        Map.Entry::getKey,  // Ключ остаётся тем же (paymentId)
                        Map.Entry::getValue  // Значение остаётся тем же (count)
                )).keySet();

        return consumptionByDateTime.stream()
                .filter(c -> !paymentIdsWhereCountMoreOne.contains(c.getTransactionId()))
                .collect(Collectors.toList());
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
