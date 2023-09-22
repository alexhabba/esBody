package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.AdministratorWorkDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdministratorWorkDayRepository extends JpaRepository<AdministratorWorkDay, UUID> {
}
