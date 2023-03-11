package com.logicaScoolBot.controller;

import com.logicaScoolBot.entity.Student;
import com.logicaScoolBot.entity.User;
import com.logicaScoolBot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class R {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/all")
    List<Student> getList() {
        return studentRepository.findAll().stream().sorted(Comparator.comparing(Student::getId)).collect(Collectors.toList());
    }

    @PostMapping("/post")
    List<Student> getList(@RequestBody User user) {
        System.out.println(user.toString());
        return studentRepository.findAll().stream().sorted(Comparator.comparing(Student::getId)).collect(Collectors.toList());
    }
}
