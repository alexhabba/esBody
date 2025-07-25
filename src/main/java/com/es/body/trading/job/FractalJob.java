//package com.es.body.trading.job;
//
//import com.es.body.trading.BinanceSymbolsFetcher;
//import com.es.body.trading.service.FractalService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class FractalJob {
//
//    private final BinanceSymbolsFetcher binanceSymbolsFetcher;
//    private final FractalService fractalService;
//
//    @Scheduled(cron = "0 */15 * * * *")  // Запуск каждые 15 минут
//    public void executeEveryFifteenMinutes() {
//        int count = 27;
//        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");
//        symbols.forEach(symbol -> fractalService.getAndSaveFractal(symbol, "15m", count));
//    }
//
//        @Scheduled(cron = "0 0 * * * *")
//    public void executeOneHour() {
//        int count = 27;
//        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");
//
//        symbols.forEach(symbol -> fractalService.getAndSaveFractal(symbol, "1h", count));
//    }
//
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void executeDay() {
//        int count = 27;
//        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");
//
//        symbols.forEach(symbol -> fractalService.getAndSaveFractal(symbol, "1d", count));
//    }
//
//}
