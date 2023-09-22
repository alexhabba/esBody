package com.logicaScoolBot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
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

    private String nameAdder;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Qr> qrc;

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
