package com.logicaScoolBot.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Service
public class ReadEmails {

    @SneakyThrows
    public Map<LocalDateTime, String> main() {
        Map<LocalDateTime, String> map = new HashMap<>();
        //Объект properties содержит параметры соединения
        Properties properties = new Properties();
        //Так как для чтения Yandex требует SSL-соединения - нужно использовать фабрику SSL-сокетов
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        //Создаем соединение для чтения почтовых сообщений
        Session session = Session.getDefaultInstance(properties);
        //Это хранилище почтовых сообщений. По сути - это и есть почтовый ящик=)
        try (Store store = session.getStore("imap")) {
            //Для чтения почтовых сообщений используем протокол IMAP.
            //Почему? Так Yandex сказал: https://yandex.ru/support/mail/mail-clients.html
            //см. раздел "Входящая почта"
            //Подключаемся к почтовому ящику
            store.connect("imap.yandex.ru", 993, "msk_sever@algoritmika.org", "yogowbtnwsqckqvg");
            //Это папка, которую будем читать
            try (Folder inbox = store.getFolder("INBOX")) {
                //Читаем папку "Входящие сообщения"
                //Будем только читать сообщение, не меняя их
                inbox.open(Folder.READ_WRITE);

                //Получаем количество сообщения в папке
                int count = inbox.getMessageCount();
                //Вытаскиваем все сообщения с первого по последний
                Message[] messages = inbox.getMessages(1, count);

                //Циклом пробегаемся по всем сообщениям
                for (Message message : messages) {
                    String from = ((InternetAddress) message.getFrom()[0]).getAddress();
                    if ("no-reply@tochka.com".equals(from) && message.getSubject() != null
                            && message.getContent() != null
                            && message.getSubject().contains("Поступил платёж через")
                    ) {
                        LocalDateTime receivedDate = LocalDateTime.ofInstant(message.getReceivedDate().toInstant(), ZoneId.systemDefault());
                        Multipart content = (Multipart) message.getContent();
                        BodyPart bodyPart = content.getBodyPart(0);
                        Multipart content1 = (Multipart) bodyPart.getContent();
                        BodyPart bodyPart1 = content1.getBodyPart(0);
                        Object content2 = bodyPart1.getContent();

                        map.put(receivedDate, content2.toString());
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
