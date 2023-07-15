package com.logicaScoolBot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

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
