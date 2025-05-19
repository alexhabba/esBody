package com.logicaScoolBot.controller;

import com.logicaScoolBot.entity.Client;
import com.logicaScoolBot.service.ClientService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TestController {

    private final ClientService clientService;

    @GetMapping("/")
    public String test() {
        return "пока не реализовано";
    }

    @Timed("allUser")
    @GetMapping("/allUser")
    public List<Client> getAllUser() {
        return clientService.getAllClient();
    }
}
