package com.logicaScoolBot.controller;

import com.logicaScoolBot.entity.Payment;
import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.repository.PaymentRepository;
import com.logicaScoolBot.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TestController {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    @GetMapping("/")
    public String test() {
        return paymentRepository.findAll().stream()
                .map(Payment::getAmount)
                .map(Integer::valueOf)
                .reduce(0, Integer::sum).toString();
    }

    @GetMapping("/allUser")
    public List<Student> getAllUser() {
        return studentRepository.findAll();
    }
}
