package com.es.body.repository;

import com.es.body.enums.Role;
import com.es.body.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<TelegramUser, Long> {

    List<TelegramUser> findAllByRole(Role role);
}
