package com.es.body.service;

import com.es.body.dto.BalanceResponse;
import com.es.body.enums.OrgType;
import com.es.body.statement.dto.ResponseStatementDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.es.body.statement.Utils.getResponse;

@Service
@RequiredArgsConstructor
public class BalanceInfoService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public double getBalance(String accountId, String token) {
        Request request = new Request.Builder()
                .url("https://enter.tochka.com/uapi/open-banking/v1.0/accounts/" + accountId + "/balances")
                .addHeader("Authorization", token)
                .build();
        String response = getResponse(request, 3);
        Double balance = objectMapper.readValue(response, BalanceResponse.class).getData().getBalance()
                .stream()
                .filter(b -> "OpeningAvailable".equals(b.getType()))
                .map(BalanceResponse.Balance::getAmount)
                .map(BalanceResponse.Balance.Amount::getAmount)
                .findAny().get();
        return balance;
    }
}
