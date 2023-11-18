package com.logicaScoolBot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {

    private ReplyKeyboardMarkup createButtonList(List<String> listButton) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(listButton.get(0));
        row.add(listButton.get(1));
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(listButton.get(2));
        row.add(listButton.get(3));
        keyboardRows.add(row);
        if (listButton.size() > 4) {
            row = new KeyboardRow();
            row.add(listButton.get(4));
            keyboardRows.add(row);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
