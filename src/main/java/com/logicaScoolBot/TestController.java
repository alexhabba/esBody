package com.logicaScoolBot;

import com.logicaScoolBot.entity.Payment;
import com.logicaScoolBot.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/")
    public String test() {
        return paymentRepository.findAll().stream()
                .map(Payment::getAmount)
                .map(Integer::valueOf)
                .reduce(0, Integer::sum).toString();
    }
}
