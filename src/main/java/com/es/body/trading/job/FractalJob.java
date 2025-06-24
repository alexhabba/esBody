package com.es.body.trading.job;

import com.es.body.trading.BinanceSymbolsFetcher;
import com.es.body.trading.CandleApi;
import com.es.body.trading.entity.Candle;
import com.es.body.trading.entity.Fractal;
import com.es.body.trading.repository.FractalRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class FractalJob {

    private final BinanceSymbolsFetcher binanceSymbolsFetcher;
    private final FractalRepository fractalRepository;

//    @Scheduled(fixedDelay = 1000)
    public void execute() {
        int count = 27;
        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");
        symbols.forEach(s -> {
            List<Candle> candlesH1 = CandleApi.getCandleWithoutTicks(s, LocalDateTime.now(), "1H", 1000).stream()
                    .sorted(comparing(Candle::getCreateDate))
                    .collect(toList());

            extracted(s, candlesH1, count, "1h");

            List<Candle> candlesH4 = CandleApi.getCandleWithoutTicks(s, LocalDateTime.now(), "4h", 1000).stream()
                    .sorted(comparing(Candle::getCreateDate))
                    .collect(toList());

            extracted(s, candlesH4, count, "4h");

            List<Candle> candlesD1 = CandleApi.getCandleWithoutTicks(s, LocalDateTime.now(), "1d", 1000).stream()
                    .sorted(comparing(Candle::getCreateDate))
                    .collect(toList());

            extracted(s, candlesD1, count, "1d");

        });

        System.out.println();
    }

    private void extracted(String s, List<Candle> candles, int count, String interval) {
        for (int i = 0; i <= candles.size() - count; i++) {
            // Берем подсписок из 27 свечей (окно)
            List<Candle> window = candles.subList(i, i + count);

            // Обрабатываем окно
            getAndSaveFractal(s, interval, new ArrayList<>(window), count);
        }
    }

        @Scheduled(cron = "0 0 * * * *")
    public void executeOneHour() {
        int count = 27;
        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");

        symbols.forEach(symbol -> this.getAndSaveFractal(symbol, "1h", count));
    }

        @Scheduled(cron = "0 0 */4 * * *")
    public void executeFourHour() {
        int count = 27;
        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");

        symbols.forEach(symbol -> this.getAndSaveFractal(symbol, "4h", count));
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void executeDay() {
        int count = 27;
        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");

        symbols.forEach(symbol -> this.getAndSaveFractal(symbol, "1d", count));
    }

    @SneakyThrows
    private void getAndSaveFractal(String symbol, String interval, int count) {
        // чтобы сформировать фрактал, нужно слева и справа иметь одинаковое кол-во баров
        // фрактал 13 баров берем кол-во баров умножаем на 2 и плюс 2

        List<Candle> candles = CandleApi.getCandleWithoutTicks(symbol, LocalDateTime.now(), interval, count).stream()
                .sorted(comparing(Candle::getCreateDate))
                .collect(toList());

        if (candles.size() != 27) {
            return;
        }

        int median = candles.size() / 2;
        Candle Candle = candles.get(median);

        if (isLowFractalLeft(candles, median) && isLowFractalRight(candles, median)) {
            Fractal fractal = Fractal.builder()
                    .countCandle(count)
                    .symbol(symbol)
                    .createDate(Candle.getCreateDate())
                    .interval(interval)
                    .low(Double.toString(Candle.getLow()))
                    .build();
            saveFractal(fractal);
        }

        if (isHighFractalLeft(candles, median) && isHighFractalRight(candles, median)) {
            Fractal fractal = Fractal.builder()
                    .countCandle(count)
                    .symbol(symbol)
                    .createDate(Candle.getCreateDate())
                    .interval(interval)
                    .high(Double.toString(Candle.getHigh()))
                    .build();
            saveFractal(fractal);
        }
    }

    private void saveFractal(Fractal fractal) {
        try {
            fractalRepository.save(fractal);
        } catch (Throwable ex) {

        }
    }

    private boolean isLowFractalLeft(List<Candle> Candles, int median) {
        for (int i = 0; i < median; i++) {
            if (!(Candles.get(median).getLow() <= Candles.get(i).getLow())) {
                return false;
            }
        }
        return true;
    }

    private boolean isLowFractalRight(List<Candle> Candles, int median) {
        for (int i = median + 1; i < Candles.size(); i++) {
            if (!(Candles.get(median).getLow() < Candles.get(i).getLow())) {
                return false;
            }
        }
        return true;
    }

    private boolean isHighFractalLeft(List<Candle> Candles, int median) {
        for (int i = 0; i < median; i++) {
            if (!(Candles.get(median).getHigh() >= Candles.get(i).getHigh())) {
                return false;
            }
        }
        return true;
    }

    private boolean isHighFractalRight(List<Candle> Candles, int median) {
        for (int i = median + 1; i < Candles.size(); i++) {
            if (!(Candles.get(median).getHigh() > Candles.get(i).getHigh())) {
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    private void getAndSaveFractal(String symbol, String interval, List<Candle> candles, int count) {
        // чтобы сформировать фрактал, нужно слева и справа иметь одинаковое кол-во баров
        // фрактал 13 баров берем кол-во баров умножаем на 2 и плюс 2

        if (candles.size() != 27) {
            return;
        }

        int median = candles.size() / 2;
        Candle Candle = candles.get(median);

        if (isLowFractalLeft(candles, median) && isLowFractalRight(candles, median)) {
            Fractal fractal = Fractal.builder()
                    .countCandle(count)
                    .symbol(symbol)
                    .createDate(Candle.getCreateDate())
                    .interval(interval)
                    .low(Double.toString(Candle.getLow()))
                    .build();
            saveFractal(fractal);
        }

        if (isHighFractalLeft(candles, median) && isHighFractalRight(candles, median)) {
            Fractal fractal = Fractal.builder()
                    .countCandle(count)
                    .symbol(symbol)
                    .createDate(Candle.getCreateDate())
                    .interval(interval)
                    .high(Double.toString(Candle.getHigh()))
                    .build();
            saveFractal(fractal);
        }
    }
}
