package com.es.body.entity;

import com.es.body.enums.QrStatus;
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
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Qr {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String qrcId;

    private String purpose;

    private long amount;

    @Enumerated(EnumType.STRING)
    private QrStatus status;

    private String nameAdder;

    private boolean isSend;

    @CreationTimestamp
    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @ManyToOne
    private Client client;

}
