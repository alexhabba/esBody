package com.logicaScoolBot.service;

import com.logicaScoolBot.entity.Consumption;
import com.logicaScoolBot.repository.ConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements ConsumptionService {

    private final ConsumptionRepository repository;

    @Override
    public Consumption save(Consumption consumption) {
        return repository.save(consumption);
    }

    @Override
    public int getAmountMonth(LocalDateTime dateTime) {
        return repository.getAmountMonth(dateTime);
    }
}
