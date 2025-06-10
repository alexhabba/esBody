package com.es.body.service;

import com.es.body.entity.TelegramUser;
import com.es.body.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.es.body.enums.Role.TRADER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "trader")
    public List<TelegramUser> findRoleByTrader() {
        return userRepository.findAllByRoles(List.of(TRADER));
    }
}
