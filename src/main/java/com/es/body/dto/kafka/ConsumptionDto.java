package com.es.body.dto.kafka;

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
public class ConsumptionDto implements KafkaEvent {

    private UUID id;
    private String description;
    private long amount;
    private boolean isSend;
    private LocalDateTime createDate;
}
