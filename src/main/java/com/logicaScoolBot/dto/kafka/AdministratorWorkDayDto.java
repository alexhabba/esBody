package com.logicaScoolBot.dto.kafka;

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
public class AdministratorWorkDayDto implements KafkaEvent {

    private UUID id;
    private long chatId;
    private String name;
    private boolean isSend;
    private LocalDateTime createDate;
}
