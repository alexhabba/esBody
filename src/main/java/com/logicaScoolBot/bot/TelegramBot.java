package com.logicaScoolBot.bot;

import com.logicaScoolBot.config.BotConfig;
import com.logicaScoolBot.entity.AdministratorWorkDay;
import com.logicaScoolBot.enums.City;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.enums.Role;
import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.entity.TelegramUser;
import com.logicaScoolBot.repository.QrRepository;
import com.logicaScoolBot.repository.StudentRepository;
import com.logicaScoolBot.repository.UserRepository;
import com.logicaScoolBot.service.AdministratorWorkDayService;
import com.logicaScoolBot.service.BeenResolver;
import com.logicaScoolBot.service.ConsumptionService;
import com.logicaScoolBot.service.SbpService;
import com.vdurmont.emoji.EmojiParser;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.logicaScoolBot.enums.City.COMMON;
import static com.logicaScoolBot.enums.City.DUBNA;
import static com.logicaScoolBot.enums.City.MOSCOW;
import static com.logicaScoolBot.enums.City.RAMENSKOE;
import static com.logicaScoolBot.enums.City.VOSKRESENSK;
import static com.logicaScoolBot.enums.Role.ADMIN;
import static com.logicaScoolBot.enums.Role.ADMIN_DUBNA;
import static com.logicaScoolBot.enums.Role.ADMIN_MOSKOW;
import static com.logicaScoolBot.enums.Role.ADMIN_RAMENSKOE;
import static com.logicaScoolBot.enums.Role.ADMIN_TEST;
import static com.logicaScoolBot.enums.Role.ADMIN_VOSKRESENSK;
import static com.logicaScoolBot.enums.Role.SUPER_ADMIN;
import static com.logicaScoolBot.enums.Role.TEACHER_DUBNA;
import static com.logicaScoolBot.enums.Role.TEACHER_MOSKOW;
import static com.logicaScoolBot.enums.Role.TEACHER_RAMENSKOE;
import static com.logicaScoolBot.enums.Role.TEACHER_TEST;
import static com.logicaScoolBot.enums.Role.TEACHER_VOSKRESENSK;
import static com.logicaScoolBot.service.AdministratorWorkDayServiceImpl.REPEAT_CLICK;
import static com.logicaScoolBot.utils.PhoneUtils.getPhoneFormat;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final SbpService sbpService;
    private static final String QR_GENERATE = "Сгенерировать QR";
    private static final String ADD_NEW_STUDENT = "Добавление нового ученика";
    private static final String STARTED_WORK = "Приступил к работе";
    private static final String END_LESSON = "Закончил урок";

    private final UserRepository userRepository;
    private final BotConfig config;
    private final AdministratorWorkDayService administratorWorkDayService;
    private final BeenResolver beenResolver;

    private final List<Role> ADMIN_STATISTIC_DAYS = List.of(
            ADMIN_DUBNA,
            ADMIN_MOSKOW,
            ADMIN_VOSKRESENSK,
            ADMIN_RAMENSKOE,
            ADMIN_TEST
    );

    private final List<Role> TEACHER_ROLES = List.of(
            TEACHER_DUBNA,
            TEACHER_MOSKOW,
            TEACHER_VOSKRESENSK,
            TEACHER_RAMENSKOE,
            TEACHER_TEST
    );

    private final List<Role> ADMIN_START_WORK = List.of(
            SUPER_ADMIN,
            ADMIN
    );

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config,
                       UserRepository userRepository,
                       SbpService sbpService,
                       AdministratorWorkDayService administratorWorkDayService,
                       BeenResolver beenResolver) {
        this.config = config;
        this.userRepository = userRepository;
        this.sbpService = sbpService;
        this.administratorWorkDayService = administratorWorkDayService;
        this.beenResolver = beenResolver;

        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Добро пожаловать!"));
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);
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
            if (chatId == 5347044474L) {
                prepareAndSendMessage(chatId, "С этого аккаунта запрещено добавлять учеников и создавать QR");
                return;
            }

            beenResolver.resolve(update);
            String messageText = update.getMessage().getText();

            Optional<TelegramUser> byId = userRepository.findById(chatId);


            //todo это тут нахрена ???
            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (TelegramUser telegramUser : users) {
                    prepareAndSendMessage(telegramUser.getChatId(), textToSend);
                }
            } else {
                if (messageText.equals(QR_GENERATE)) {
                    prepareAndSendMessage(chatId, "Для того чтоб сгенерировать QR, необходимо отправить сообщение с суммой и номером телефона клиента, для которого хотим сгенерировать QR.\n" +
                            "например:\n" +
                            "QR 1000 9273888212");
                } else if (messageText.startsWith("QR")) {
                    //todo проверить что соответствует формату "QR 1000 9273888212"
                    String[] strArr = messageText.split("\\s+");
                    int amount = Integer.parseInt(strArr[1]) * 100;
                    String purpose = getPhoneFormat(strArr[2]);
                    TelegramUser telegramUser = userRepository.findById(chatId).orElseThrow();
                    String payload = sbpService.registerQr(amount, purpose, telegramUser.getFirstName());
                    prepareAndSendMessage(chatId, payload);
                }

                switch (messageText) {
                    case "/start":

                        registerUser(update.getMessage());
                        break;
                    case END_LESSON:
                        // todo implement logic

                        break;

                    case STARTED_WORK:
                        if (byId.isPresent() && nonNull(byId.get().getRole()) && ADMIN_STATISTIC_DAYS.contains(byId.get().getRole())) {
                            AdministratorWorkDay administratorWorkDay = AdministratorWorkDay.builder()
                                    .chatId(chatId)
                                    .name(byId.get().getFirstName())
                                    .build();
                            String result = administratorWorkDayService.createAdministratorWorkDay(administratorWorkDay);
                            prepareAndSendMessage(chatId, result);

                            if (!REPEAT_CLICK.equals(result)) {
                                userRepository.findAll().stream()
                                        .filter(user -> nonNull(user.getRole()) && ADMIN_START_WORK.contains(user.getRole()))
                                        .forEach(us -> prepareAndSendMessage(us.getChatId(),
                                                byId.get().getRole().toString() + " " + byId.get().getFirstName() + " приступил к работе"));
                            }
                        }
                        break;
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)) {
                String text = "You pressed YES button";
                executeEditMessageText(text, chatId, messageId);
            } else if (callbackData.equals(NO_BUTTON)) {
                String text = "You pressed NO button";
                executeEditMessageText(text, chatId, messageId);
            }
        }
    }

    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
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
        } else {
            sendButtonStartWork(byId.get());
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
        row.add(ADD_NEW_STUDENT);

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void sendButtonStartWork(long chatId, String textToSend, String button) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        if (END_LESSON.equals(button)) {
            row.add(button);
        } else {
            row.add(QR_GENERATE);
            row.add(ADD_NEW_STUDENT);

            row.add(button);
        }

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
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

    private void sendButtonStartWork(TelegramUser user) {
        Role role = user.getRole();
        if (nonNull(role) && ADMIN_STATISTIC_DAYS.contains(role) && !user.isSendButtonStartWork()) {
            sendButtonStartWork(user.getChatId(),
                    "ВАЖНО!!!\nДобавлена кнопка \"" + STARTED_WORK + "\"," +
                            " когда вы приступаете к работе необходимо ее нажать для учета ваших рабочих дней.", STARTED_WORK
            );
            user.setSendButtonStartWork(true);
            userRepository.save(user);
        } else if (nonNull(role) && TEACHER_ROLES.contains(role) && !user.isSendButtonStartWork()) {
            sendButtonStartWork(user.getChatId(),
                    "ВАЖНО!!!\nДобавлена кнопка \"" + END_LESSON + "\"," +
                            " когда вы закончили урок, необходимо ее нажать для учета ваших уроков.", END_LESSON);
            user.setSendButtonStartWork(true);
            userRepository.save(user);
        }
    }

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
