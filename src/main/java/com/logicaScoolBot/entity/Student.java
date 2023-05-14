package com.logicaScoolBot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
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
