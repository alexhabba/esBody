//package com.es.body.trading.job;
//
//import com.es.body.trading.CandleService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class TradingJob {
//
//    private final CandleService candleService;
//
//    @Scheduled(fixedDelay = 3000)
//    public void executeJob() {
//        candleService.run();
//    }
//}
