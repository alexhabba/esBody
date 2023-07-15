package com.logicaScoolBot.dto;

import com.logicaScoolBot.dto.DataDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RequestQrRegistrationDto {
    private DataDto Data;
}
