package com.logicaScoolBot.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Service
@RequiredArgsConstructor
public class SenderService {

    private final ApplicationContext applicationContext;

    @SneakyThrows
    public void send(Long chatId, String textMessage) {
        AbsSender bean = applicationContext.getBean(AbsSender.class);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textMessage);
        bean.execute(message);
    }

}
