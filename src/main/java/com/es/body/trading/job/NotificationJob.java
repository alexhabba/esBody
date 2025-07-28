package com.es.body.trading.job;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.es.body.service.SenderService;
import com.es.body.trading.CandleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class NotificationJob {

    private final ObjectMapper objectMapper;
    private final SenderService senderService;

    @Scheduled(cron = "* * */3 * * *")
    public void executeJob() {
        showPositionAndBalance();
    }

    @SneakyThrows
    private void showPositionAndBalance() {
        BigDecimal balance = getBalance("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe");
        senderService.send(1466178855L, "Сейчас на счете " + balance);
    }

    @SneakyThrows
    public BigDecimal getBalance(String key, String secret) {
        var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newAccountRestClient();
        AccountDataRequest request = AccountDataRequest.builder().accountType(AccountType.UNIFIED).coin("USDT").build();
        Object walletBalance = client.getWalletBalance(request);
        String string = objectMapper.writeValueAsString(walletBalance);
        WalletBalanceDto wallet = objectMapper.readValue(string, WalletBalanceDto.class);
        return wallet.getResult().getList().get(0).getTotalEquity();
    }

    public static void main(String[] args) {
        NotificationJob notificationJob = new NotificationJob(new ObjectMapper(), null);
        notificationJob.showPositionAndBalance();
    }
}
