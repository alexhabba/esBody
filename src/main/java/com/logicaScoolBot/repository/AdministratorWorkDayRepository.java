package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.AdministratorWorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdministratorWorkDayRepository extends JpaRepository<AdministratorWorkDay, UUID> {

    @Query("select a from AdministratorWorkDay a where a.isSend = false ")
    List<AdministratorWorkDay> findAllByNotSend();

    @Query("select a from AdministratorWorkDay a where a.chatId = :chatId and a.createDate > :createDate")
    Optional<AdministratorWorkDay> findAdministratorWorkDayToday(
            @Param("chatId") Long chatId,
            @Param("createDate")LocalDateTime createDate
    );
}
