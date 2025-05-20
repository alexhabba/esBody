package com.es.body.service;

import com.es.body.exception.HandlerMessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeenResolver {

    private final List<HandlerMessage> handlers;

    public void resolve(Update update) {
        try {
            handlers.forEach(h -> h.handle(update));
        } catch (HandlerMessageException ignored) {
        }
    }
}
