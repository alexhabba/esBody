package com.logicaScoolBot.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Salary {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private int amount;

    /**
     * Пример: Аванс за декабрь 2023 или Зарплата за январь 2024
     */
    private String description;

    private Long chatId;

    @CreationTimestamp
    private LocalDateTime createDate;

    private boolean isSend;
}
