package com.logicaScoolBot.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "student_2")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String phone;

    private String fullNameChild;

    private String fullNameParent;

    private String city;

    private String course;

    @Override
    public String toString() {
        return
                "телефон : " + phone + "\n" +
                "имя ребенка : " + fullNameChild + "\n" +
                "имя родителя : " + fullNameParent + "\n" +
                "курс : " + course + "\n" +
                "город : " + city;
    }

}
