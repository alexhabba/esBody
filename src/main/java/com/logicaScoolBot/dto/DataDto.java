package com.logicaScoolBot.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DataDto {
    private String merchantId;
    private String legalId;
    private BigDecimal amount;
    private String currency;
    private String paymentPurpose;
    private String qrcType;
    private String sourceName;

}
