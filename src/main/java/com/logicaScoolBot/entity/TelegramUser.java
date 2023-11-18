package com.logicaScoolBot.entity;

import com.logicaScoolBot.enums.Role;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity
public class TelegramUser {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String phone;

    private String bank;

    private boolean isSend;

    /**
     * Признак отправки в кафку
     */
    private boolean isSendButtonStartWork;

    private Timestamp registeredAt;

}
