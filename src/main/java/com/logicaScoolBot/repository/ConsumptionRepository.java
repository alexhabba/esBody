package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsumptionRepository extends JpaRepository<Consumption, UUID> {
}
