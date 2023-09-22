package com.logicaScoolBot.service;

import com.logicaScoolBot.entity.AdministratorWorkDay;
import com.logicaScoolBot.repository.AdministratorWorkDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdministratorWorkDayServiceImpl implements AdministratorWorkDayService {

    private final AdministratorWorkDayRepository repository;

    @Override
    public AdministratorWorkDay createAdministratorWorkDay(AdministratorWorkDay entity) {
        return repository.save(entity);
    }
}
