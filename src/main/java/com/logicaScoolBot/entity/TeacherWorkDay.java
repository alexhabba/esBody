package com.logicaScoolBot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
//@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherWorkDay {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private long chatId;

    @Enumerated(EnumType.STRING)
    private Course course;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isSend;

    @CreationTimestamp
    private LocalDateTime createDate;
}
