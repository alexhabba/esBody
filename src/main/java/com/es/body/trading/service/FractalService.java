package com.es.body.trading.service;

import com.es.body.trading.CandleApi;
import com.es.body.trading.entity.Candle;
import com.es.body.trading.entity.Fractal;
import com.es.body.trading.repository.FractalRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class FractalService {

    private final FractalRepository fractalRepository;

    @SneakyThrows
    public void getAndSaveFractal(String symbol, String interval, int count) {
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

    public void saveFractal(Fractal fractal) {
        try {
            fractalRepository.save(fractal);
        } catch (Throwable ex) {

        }
    }

    public static boolean isLowFractalLeft(List<Candle> Candles, int median) {
        for (int i = 0; i < median; i++) {
            if (!(Candles.get(median).getLow() <= Candles.get(i).getLow())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLowFractalRight(List<Candle> Candles, int median) {
        for (int i = median + 1; i < Candles.size(); i++) {
            if (!(Candles.get(median).getLow() < Candles.get(i).getLow())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHighFractalLeft(List<Candle> Candles, int median) {
        for (int i = 0; i < median; i++) {
            if (!(Candles.get(median).getHigh() >= Candles.get(i).getHigh())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHighFractalRight(List<Candle> Candles, int median) {
        for (int i = median + 1; i < Candles.size(); i++) {
            if (!(Candles.get(median).getHigh() > Candles.get(i).getHigh())) {
                return false;
            }
        }
        return true;
    }

    public List<Fractal> getFractals(String interval) {
        return fractalRepository.findFractalByInterval(interval);
    }
}
