package com.logicaScoolBot.service;

import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.exception.HandlerMessageException;
import com.logicaScoolBot.repository.StudentRepository;
import com.logicaScoolBot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Comparator;
import java.util.List;

import static com.logicaScoolBot.constnt.Constant.ADD_NEW_STUDENT;
import static com.logicaScoolBot.utils.PhoneUtils.getPhoneFormat;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService, HandlerMessage {

    private final SenderService senderService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public List<Student> getAllStudent() {
        return studentRepository.findAll();
    }

    @Override
    public void handle(Update update) {
        String message = update.getMessage().getText();
        if (message.startsWith(ADD_NEW_STUDENT) && message.length() > ADD_NEW_STUDENT.length()) {
            String[] split = message.split("\n");
            String phone = getPhoneFormat(split[4]);
            if (phone.length() != 10) {
                senderService.send(update.getMessage().getChatId(), "Неверный формат телефона");
                throw new HandlerMessageException();
            }
            Student studentByPhone = studentRepository.findStudentByPhone(phone);
            if (studentByPhone != null) {
                senderService.send(update.getMessage().getChatId(), "Такой студент уже есть в базе");
                throw new HandlerMessageException();
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
                    .nameAdder(userRepository.findById(update.getMessage().getChatId()).orElseThrow().getFirstName())
                    .build();
            studentRepository.save(student);
            senderService.send(update.getMessage().getChatId(), "Ученик добавлен.");
        } else if (message.equals(ADD_NEW_STUDENT)) {
            senderService.send(update.getMessage().getChatId(), "Для добавления нового ученика," +
                    " необходимо отправить сообщение по шаблону:\n\n" +
                    ADD_NEW_STUDENT + "\n" +
                    "Имя фамилия ребенка\n" +
                    "Имя фамилия родителя\n" +
                    "город\n" +
                    "телефон без 8 и слитно\n" +
                    "название курса");
        }
    }


}
