package com.logicaScoolBot.service;

import com.logicaScoolBot.entity.AdministratorWorkDay;
import com.logicaScoolBot.repository.AdministratorWorkDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdministratorWorkDayServiceImpl implements AdministratorWorkDayService, HandlerMessage{

    public static final String REPEAT_CLICK = "Вы уже сегодня нажимали эту кнопку";

    private final AdministratorWorkDayRepository repository;

    @Override
    public String createAdministratorWorkDay(AdministratorWorkDay entity) {
        Optional<AdministratorWorkDay> administratorWorkDayToday = repository.findAdministratorWorkDayToday(
                entity.getChatId(), LocalDate.now().atStartOfDay());
        if (administratorWorkDayToday.isPresent()) {
            return REPEAT_CLICK;
        } else {
            repository.save(entity);
            return "Отлично!\nХорошего дня!";
        }
    }

    @Override
    public void handle(Update update) {

    }
}
