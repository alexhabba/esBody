package com.es.body.repository;

import com.es.body.entity.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ConsumptionRepository extends JpaRepository<Consumption, UUID> {

    @Query("select c from Consumption c where c.isSend = false ")
    List<Consumption> findAllByNotSend();

    @Query("select sum(c.amount) from Consumption c where c.createDate >= :dateTime")
    int getAmountMonth(@Param("dateTime") LocalDateTime dateTime);

    @Query("select c.paymentId from Consumption c where c.paymentId in (:paymentIds)")
    Set<String> findAllByPaymentIds(Set<String> paymentIds);

    @Query("select c from Consumption c where c.createDate >= :dateTime")
    List<Consumption> getConsumptionByDateTime(@Param("dateTime") LocalDateTime dateTime);
}
