package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Qr;
import com.logicaScoolBot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findStudentByPhone(String phone);

    @Query("select s from Student s where s.isSend = false ")
    List<Student> findAllByNotSend();
}
