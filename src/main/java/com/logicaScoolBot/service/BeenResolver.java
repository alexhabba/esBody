package com.logicaScoolBot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class BeenResolver {

    private final ConsumptionService consumptionService;
    private final LessonService lessonService;

    public void resolve(Update update) {
//        update.getMessage().getText()
        try {
            consumptionService.handle(update);
        } catch (Exception e) {
            return;
        }
        System.out.println();
    }
}
