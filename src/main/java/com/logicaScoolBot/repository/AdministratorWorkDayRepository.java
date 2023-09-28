package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.AdministratorWorkDay;
import com.logicaScoolBot.entity.Qr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AdministratorWorkDayRepository extends JpaRepository<AdministratorWorkDay, UUID> {

    @Query("select a from AdministratorWorkDay a where a.isSend = false ")
    List<AdministratorWorkDay> findAllByNotSend();
}
