package com.logicaScoolBot.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto implements KafkaEvent {

    private Long id;
    private String phone;
    private String fullNameChild;
    private String fullNameParent;
    private String city;
    private String course;
    private String nameAdder;
    private boolean isSend;

}
