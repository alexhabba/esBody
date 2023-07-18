package com.logicaScoolBot.bot;

import com.logicaScoolBot.config.BotConfig;
import com.logicaScoolBot.entity.Payment;
import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.entity.TelegramUser;
import com.logicaScoolBot.repository.PaymentRepository;
import com.logicaScoolBot.repository.StudentRepository;
import com.logicaScoolBot.repository.UserRepository;
import com.logicaScoolBot.service.SbpService;
import com.vdurmont.emoji.EmojiParser;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final SbpService sbpService;
    private static final String QR_GENERATE = "Сгенерировать QR";
    private static final String ADD_NEW_STUDENT = "Добавление нового ученика";

    private static final String KRISTINA = "Kristina";
    private static final String ALEX = "Alex";
    private static final String VERONIKA = "VERONIKA";
    private final UserRepository userRepository;
    private final BotConfig config;
    private final StudentRepository studentRepository;
    private final PaymentRepository paymentRepository;
    private final Set<Long> chatIds = Set.of(1466178855L, 397009920L);
    private final Map<String, Long> mapChatId = Map.of(
            KRISTINA, 397009920L,
            ALEX, 1466178855L,
            VERONIKA, 5090638968L
    );

    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities.\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config,
                       UserRepository userRepository,
                       StudentRepository studentRepository,
                       PaymentRepository paymentRepository, SbpService sbpService) {
        this.config = config;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.paymentRepository = paymentRepository;
        this.sbpService = sbpService;

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
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (TelegramUser telegramUser : users) {
                    prepareAndSendMessage(telegramUser.getChatId(), textToSend);
                }
            } else {
                if (messageText.startsWith(ADD_NEW_STUDENT) && messageText.length() > ADD_NEW_STUDENT.length() ) {
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
                            .fullNameChild(split[1])
                            .fullNameParent(split[2])
                            .city(split[3])
                            .phone(phone)
                            .course(split[5])
                            .build();
                    studentRepository.save(student);
                    prepareAndSendMessage(chatId, "Ученик добавлен.");
                    System.out.println("Ученик добавлен.");
                } else if (messageText.equals(QR_GENERATE)) {
                    prepareAndSendMessage(chatId, "Для того чтоб сгенерировать QR, необходимо отправить сообщение с суммой и номером телефона клиента, для которого хотим сгенерировать QR.\n" +
                            "например:\n" +
                            "QR 1000 9273888212");
                } else if (messageText.startsWith("QR")) {
                    //todo проверить что соответствует формату "QR 1000 9273888212"
                    String[] strArr = messageText.split(" ");
                    int amount = Integer.parseInt(strArr[1]) * 100;
                    String purpose = getPhoneFormat(strArr[2]);
                    String payload = sbpService.registerQr(amount, purpose);
                    prepareAndSendMessage(chatId, payload);
                }

                switch (messageText) {
                    case "/start":

                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;

                    case "/start@LogicaScoolBot":

                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;

                    case "/help":

                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;

                    case "/register":

                        register(chatId);
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

//                    default:
//
//                        prepareAndSendMessage(chatId, "Sorry, command was not recognized");

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

        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            TelegramUser telegramUser = new TelegramUser();

            telegramUser.setChatId(chatId);
            telegramUser.setFirstName(chat.getFirstName());
            telegramUser.setLastName(chat.getLastName());
            telegramUser.setUserName(chat.getUserName());
            telegramUser.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(telegramUser);
            log.info("user saved: " + telegramUser);
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

    //    @Scheduled(cron = "${cron.job.statisticEveryDay}")
    public void invoke() {
        int dayOfMonth = LocalDateTime.now().getDayOfMonth() - 1;

        List<Payment> all = paymentRepository.findAll();

        Integer sumMonth = all.stream()
                .filter(payment -> payment.getCreateDate().isAfter(LocalDateTime.now().minusDays(dayOfMonth)))
                .map(Payment::getAmount)
                .map(Integer::valueOf)
                .reduce(0, Integer::sum);

        String amountMonth = "Сумма оплат за текущий месяц по СБП " + getFormatNumber(sumMonth);

        Integer sumDay = all.stream()
                .filter(payment -> payment.getCreateDate().isAfter(LocalDateTime.now().minusHours(24)))
                .map(Payment::getAmount)
                .map(Integer::valueOf)
                .reduce(0, Integer::sum);

        String amountDay = "Сумма оплат за текущий день по СБП " + getFormatNumber(sumDay);

        mapChatId.forEach((k, v) -> {
            prepareAndSendMessage(v, amountMonth);
            prepareAndSendMessage(v, amountDay);
        });
    }

    @Scheduled(cron = "${cron.job.statusQr}")
    public void statusQr() {
        System.out.println("зашли в метод проверки статусов");
        List<String> list = sbpService.statusQr();
        List<Qr> qrs = sbpService.getAllByQRId(list);

        qrs.forEach(qr -> {
            Student student = studentRepository.findStudentByPhone(qr.getPurpose());
            String textMessage = "Оплата: " + qr.getAmount() + "Р\n" + student.toString();
            mapChatId.values().forEach(chatId -> prepareAndSendMessage(chatId, textMessage)
            );
        });

        System.out.println();
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
