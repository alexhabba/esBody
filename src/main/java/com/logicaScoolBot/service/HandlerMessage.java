package com.logicaScoolBot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface HandlerMessage {
    void handle(Update update);
}
