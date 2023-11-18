package com.logicaScoolBot.repository;

import com.logicaScoolBot.enums.Role;
import com.logicaScoolBot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<TelegramUser, Long> {

    List<TelegramUser> findAllByRole(Role role);
}
