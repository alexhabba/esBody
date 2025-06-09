package com.es.body.trading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candle {
    private LocalDateTime createDate;
    private String symbol;
    private double volBuy;
    private double volSell;
    private double vol;
    private double open;
    private double close;
    private double low;
    private double high;
    private int interval;
}
