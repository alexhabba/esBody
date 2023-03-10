package market.habba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl {

    @Value("${spring.mail.username}")
    private String EMAIL;
    private final JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Market-habba<" + EMAIL + ">");
        message.setTo(toEmail);
        message.setText("Welcome to Sweater. Please, visit next link: http://localhost:3000");
        message.setSubject(subject);

        javaMailSender.send(message);
    }

}
