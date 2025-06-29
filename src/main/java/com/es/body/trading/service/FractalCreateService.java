package com.es.body.trading.service;

import com.es.body.trading.BinanceSymbolsFetcher;
import com.es.body.trading.CandleApi;
import com.es.body.trading.entity.Candle;
import com.es.body.trading.entity.Fractal;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.es.body.trading.service.FractalService.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class FractalCreateService {

    private final BinanceSymbolsFetcher binanceSymbolsFetcher;
    private final FractalService fractalService;

    //    @Scheduled(fixedDelay = 1000)
    public void execute() {
        int count = 27;
        List<String> symbols = binanceSymbolsFetcher.getAllSymbol("USDT");
        symbols.forEach(s -> {
            List<Candle> candlesH1 = CandleApi.getCandleWithoutTicks(s, LocalDateTime.now(), "1H", 1000).stream()
                    .sorted(comparing(Candle::getCreateDate))
                    .collect(toList());

            extracted(s, candlesH1, count, "1h");

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
            fractalService.saveFractal(fractal);
        }

        if (isHighFractalLeft(candles, median) && isHighFractalRight(candles, median)) {
            Fractal fractal = Fractal.builder()
                    .countCandle(count)
                    .symbol(symbol)
                    .createDate(Candle.getCreateDate())
                    .interval(interval)
                    .high(Double.toString(Candle.getHigh()))
                    .build();
            fractalService.saveFractal(fractal);
        }
    }
}
