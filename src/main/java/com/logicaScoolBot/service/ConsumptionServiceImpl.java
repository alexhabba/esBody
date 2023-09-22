package com.logicaScoolBot.service;

import com.logicaScoolBot.entity.Consumption;
import com.logicaScoolBot.repository.ConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements ConsumptionService {

    private final ConsumptionRepository repository;

    @Override
    public Consumption save(Consumption consumption) {
        return repository.save(consumption);
    }
}
