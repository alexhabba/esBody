package com.logicaScoolBot.repository;

import com.logicaScoolBot.entity.Payment;
import com.logicaScoolBot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
