package com.logicaScoolBot.service;

import com.logicaScoolBot.entity.Consumption;

import java.time.LocalDateTime;

public interface ConsumptionService {

    Consumption save(Consumption consumption);

    int getAmountMonth(LocalDateTime dateTime);
}
