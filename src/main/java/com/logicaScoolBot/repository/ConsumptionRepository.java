package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Consumption;
import com.logicaScoolBot.entity.Qr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConsumptionRepository extends JpaRepository<Consumption, UUID> {

    @Query("select c from Consumption c where c.isSend = false ")
    List<Consumption> findAllByNotSend();
}
