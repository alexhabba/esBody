package com.es.body.service;

import com.es.body.entity.Consumption;

import java.time.LocalDateTime;

public interface ConsumptionService {

    Consumption save(Consumption consumption);

    int getAmountMonth(LocalDateTime dateTime);

}
