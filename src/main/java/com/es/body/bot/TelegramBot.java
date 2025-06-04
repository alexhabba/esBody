package com.es.body.bot;

import com.es.body.config.BotConfig;
import com.es.body.constnt.Constant;
import com.es.body.entity.TelegramUser;
import com.es.body.enums.Role;
import com.es.body.repository.UserRepository;
import com.es.body.service.BeenResolver;
import com.es.body.service.SbpService;
import com.es.body.utils.PhoneUtils;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    public static final String YOU_HAVE_NOT_RIGHTS = "У ВАС НЕТ ПРАВ.\nНЕОБХОДИМО ПОЛУЧИТЬ НЕОБХОДИМЫЕ ПРАВА";
    private final SbpService sbpService;
    private static final String QR_GENERATE = "Сгенерировать QR";

    private final UserRepository userRepository;
    private final BotConfig config;
    private final BeenResolver beenResolver;

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config,
                       UserRepository userRepository,
                       SbpService sbpService,
                       BeenResolver beenResolver) {
        this.config = config;
        this.userRepository = userRepository;
        this.sbpService = sbpService;
        this.beenResolver = beenResolver;

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Добро пожаловать!"));
        var yesButton = new InlineKeyboardButton();

        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            beenResolver.resolve(update);
            String messageText = update.getMessage().getText();

            if (messageText.equals(QR_GENERATE)) {
                prepareAndSendMessage(chatId, "Для того чтоб сгенерировать QR, необходимо отправить сообщение с суммой и номером телефона клиента, для которого хотим сгенерировать QR.\n" +
                        "например:\n" +
                        "QR 1000 9273888212");
            } else if (messageText.startsWith("QR")) {
                // todo проверить что соответствует формату "QR 1000 9273888212"
                String[] strArr = messageText.split("\\s+");
                int amount = Integer.parseInt(strArr[1]) * 100;
                String purpose = PhoneUtils.getPhoneFormat(strArr[2]);
                TelegramUser telegramUser = userRepository.findById(chatId).orElseThrow();
                if (telegramUser.getRole() == Role.MANAGER || telegramUser.getRole() == Role.SUPER_ADMIN) {
                    String payload = sbpService.registerQr(amount, purpose, telegramUser.getFirstName());
                    prepareAndSendMessage(chatId, payload);
                } else {
                    prepareAndSendMessage(chatId, YOU_HAVE_NOT_RIGHTS);
                }
            } else if ("/start".equals(messageText)) {
                registerUser(update.getMessage());
            }
        }
    }

    private void registerUser(Message msg) {

        Optional<TelegramUser> byId = userRepository.findById(msg.getChatId());
        if (byId.isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            TelegramUser telegramUser = new TelegramUser();

            telegramUser.setChatId(chatId);
            telegramUser.setFirstName(chat.getFirstName());
            telegramUser.setLastName(chat.getLastName());
            telegramUser.setUserName(chat.getUserName());
            telegramUser.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(telegramUser);
            startCommandReceived(chatId, chat.getFirstName());
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :blush:");
        log.info("Replied to user " + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add(QR_GENERATE);
        row.add(Constant.ADD_NEW_CLIENT);

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

}
