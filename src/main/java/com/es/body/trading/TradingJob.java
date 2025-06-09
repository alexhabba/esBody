package com.es.body.trading;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradingJob {

    private final CandleService candleService;

    @Scheduled(fixedDelay = 1000)
    public void executeJob() {
        candleService.run();
    }
}
