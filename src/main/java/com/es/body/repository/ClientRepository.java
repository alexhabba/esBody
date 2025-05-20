package com.es.body.repository;

import com.es.body.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findStudentByPhone(String phone);

    @Query("select s from Client s where s.isSend = false ")
    List<Client> findAllByNotSend();
}
