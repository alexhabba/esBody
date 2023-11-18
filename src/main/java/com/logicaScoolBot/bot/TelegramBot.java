package com.logicaScoolBot.bot;

import com.logicaScoolBot.config.BotConfig;
import com.logicaScoolBot.entity.AdministratorWorkDay;
import com.logicaScoolBot.entity.City;
import com.logicaScoolBot.entity.Consumption;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.entity.Role;
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
import org.jetbrains.annotations.NotNull;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.logicaScoolBot.entity.City.COMMON;
import static com.logicaScoolBot.entity.City.DUBNA;
import static com.logicaScoolBot.entity.City.MOSCOW;
import static com.logicaScoolBot.entity.City.RAMENSKOE;
import static com.logicaScoolBot.entity.City.VOSKRESENSK;
import static com.logicaScoolBot.entity.Role.ADMIN;
import static com.logicaScoolBot.entity.Role.ADMIN_DUBNA;
import static com.logicaScoolBot.entity.Role.ADMIN_MOSKOW;
import static com.logicaScoolBot.entity.Role.ADMIN_RAMENSKOE;
import static com.logicaScoolBot.entity.Role.ADMIN_TEST;
import static com.logicaScoolBot.entity.Role.ADMIN_VOSKRESENSK;
import static com.logicaScoolBot.entity.Role.SUPER_ADMIN;
import static com.logicaScoolBot.entity.Role.TEACHER_DUBNA;
import static com.logicaScoolBot.entity.Role.TEACHER_MOSKOW;
import static com.logicaScoolBot.entity.Role.TEACHER_RAMENSKOE;
import static com.logicaScoolBot.entity.Role.TEACHER_TEST;
import static com.logicaScoolBot.entity.Role.TEACHER_VOSKRESENSK;
import static com.logicaScoolBot.service.AdministratorWorkDayServiceImpl.REPEAT_CLICK;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final SbpService sbpService;
    private static final String QR_GENERATE = "Сгенерировать QR";
    private static final String ADD_NEW_STUDENT = "Добавление нового ученика";
    private static final String STARTED_WORK = "Приступил к работе";
    private static final String END_LESSON = "Закончил урок";

    private static final String KRISTINA = "Kristina";
    private static final String ALEX = "Alex";
    private static final String VERONIKA = "VERONIKA";

    private final UserRepository userRepository;
    private final BotConfig config;
    private final StudentRepository studentRepository;
    private final QrRepository qrRepository;
    private final ConsumptionService consumptionService;
    private final AdministratorWorkDayService administratorWorkDayService;
    private final BeenResolver beenResolver;

    private final Map<String, Long> mapChatId = Map.of(
            KRISTINA, 397009920L,
            ALEX, 1466178855L
    );

    private final Map<String, City> MAP_CITY = Map.of(
            "ДУБНА", DUBNA,
            "МОСКВА", MOSCOW,
            "ВОСКРЕСЕНСК", VOSKRESENSK,
            "РАМЕНСКОЕ", RAMENSKOE,
            "ОБЩИЙ", COMMON
    );

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
                       StudentRepository studentRepository,
                       SbpService sbpService,
                       QrRepository qrRepository,
                       ConsumptionService consumptionService,
                       AdministratorWorkDayService administratorWorkDayService,
                       BeenResolver beenResolver) {
        this.config = config;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.sbpService = sbpService;
        this.qrRepository = qrRepository;
        this.consumptionService = consumptionService;
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
            beenResolver.resolve(update);
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            Optional<TelegramUser> byId = userRepository.findById(chatId);

            if (chatId == 5347044474L) {
                prepareAndSendMessage(chatId, "С этого аккаунта запрещено добавлять учеников и создавать QR");
                return;
            }

            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (TelegramUser telegramUser : users) {
                    prepareAndSendMessage(telegramUser.getChatId(), textToSend);
                }
            } else {
                if (messageText.startsWith(ADD_NEW_STUDENT) && messageText.length() > ADD_NEW_STUDENT.length()) {
                    String[] split = messageText.split("\n");
                    String phone = getPhoneFormat(split[4]);
                    if (phone.length() != 10) {
                        prepareAndSendMessage(chatId, "Неверный формат телефона");
                        return;
                    }
                    Student studentByPhone = studentRepository.findStudentByPhone(phone);
                    if (studentByPhone != null) {
                        prepareAndSendMessage(chatId, "Такой студент уже есть в базе");
                        return;
                    }
                    Student student = Student.builder()
                            .id(studentRepository.findAll().stream()
                                    .map(Student::getId)
                                    .max(Comparator.naturalOrder())
                                    .orElse(0L) + 1)
                            .fullNameChild(split[1].trim())
                            .fullNameParent(split[2].trim())
                            .city(split[3].trim())
                            .phone(phone.trim())
                            .course(split[5].trim())
                            .nameAdder(userRepository.findById(chatId).orElseThrow().getFirstName())
                            .build();
                    studentRepository.save(student);
                    prepareAndSendMessage(chatId, "Ученик добавлен.");
                } else if (messageText.equals(QR_GENERATE)) {
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
                    case ADD_NEW_STUDENT:
                        prepareAndSendMessage(chatId, "Для добавления нового ученика," +
                                " необходимо отправить сообщение по шаблону:\n\n" +
                                ADD_NEW_STUDENT + "\n" +
                                "Имя фамилия ребенка\n" +
                                "Имя фамилия родителя\n" +
                                "город\n" +
                                "телефон без 8 и слитно\n" +
                                "название курса");
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

    private String getPhoneFormat(String phone) {
        phone = phone.replace("+", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "");
        return "7".equals(phone.substring(0, 1)) || "8".equals(phone.substring(0, 1)) ? phone.substring(1) : phone;
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

    @Timed("statisticEveryDay")
    @Scheduled(cron = "${cron.job.statisticEveryDay}")
    public void invoke() {
        LocalDateTime dateTimeMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).atStartOfDay();
        Integer amountSumMonth = qrRepository.getAmountSumToMonth(dateTimeMonth);
        String amountMonth = "Сумма оплат за текущий месяц по СБП " + getFormatNumber(amountSumMonth);

        int amountSumMonthConsumption = consumptionService.getAmountMonth(dateTimeMonth);
        String amountMonthConsumption = "Расход за текущий месяц " + getFormatNumber(amountSumMonthConsumption);

        LocalDateTime dateTimeDay = LocalDate.now().atStartOfDay();
        Integer amountSumDay = qrRepository.getAmountSumToDay(dateTimeDay);
        String amountDay = "Сумма оплат за текущий день по СБП " + getFormatNumber(amountSumDay);

        mapChatId.forEach((k, v) -> {
            prepareAndSendMessage(v, amountMonth);
            prepareAndSendMessage(v, amountMonthConsumption);
            prepareAndSendMessage(v, amountDay);
        });
    }

    @Timed("statusQr")
    @Scheduled(cron = "${cron.job.statusQr}")
    public void statusQr() {
//        System.out.println("statusQr");
        List<String> list = sbpService.statusQr();
        List<Qr> qrs = sbpService.getAllByQrId(list);

        qrs.forEach(qr -> {
            Student student = studentRepository.findStudentByPhone(qr.getPurpose());
            String textMessage = "Оплата: " + qr.getAmount() + "Р\n" + student.toString();
            userRepository.findAll().stream()
                    .map(TelegramUser::getChatId)
                    .forEach(chatId -> prepareAndSendMessage(chatId, textMessage));
        });
//        prepareAndSendMessage(mapChatId.get(ALEX), "hello");
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

    private String getFormatNumber(int number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        // вот тут устанавливаем разделитель он и так по умолчанию пробел,
        // но в этом примере я решил это сделать явно
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        // указываем сколько символов в группе
        df.setGroupingSize(3);
        return df.format(number);
    }
}
