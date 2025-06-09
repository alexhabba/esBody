package com.es.body.service;

import com.es.body.entity.Consumption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ConsumptionService {

    void save(Consumption consumption);

    void saveAll(List<Consumption> consumptions);

    int getAmountMonth(LocalDateTime dateTime);

    /**
     * Получение transactionIds которых нет в базе данных.
     */
    Set<String> findMissingPaymentIds(Set<String> transactionIds);

    List<Consumption> getConsumptionByDateTime(LocalDateTime dateTime);


}
