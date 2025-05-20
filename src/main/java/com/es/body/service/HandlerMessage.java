package com.es.body.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface HandlerMessage {
    void handle(Update update);
}
