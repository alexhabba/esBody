package com.logicaScoolBot.dto.kafka;

import com.logicaScoolBot.entity.QrStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrDto implements KafkaEvent {

    private UUID id;
    private String qrcId;
    private String purpose;
    private long amount;
    private QrStatus status;
    private String nameAdder;
    private boolean isSend;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
