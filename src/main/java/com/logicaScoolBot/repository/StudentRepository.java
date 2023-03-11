package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
