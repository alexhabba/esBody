package com.es.body.trading.service;

import com.es.body.entity.TelegramUser;
import com.es.body.service.SenderService;
import com.es.body.service.UserService;
import com.es.body.trading.entity.Candle;
import com.es.body.trading.entity.Fractal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class FractalD1Notification {

    private Map<String, List<Fractal>> MAP_SYMBOL_FRACTALS;

    private final UserService userService;
    private final SenderService senderService;
    private final FractalService fractalService;

    @PostConstruct
    public void init() {
        MAP_SYMBOL_FRACTALS = fractalService.getFractals("1d").stream()
                .collect(Collectors.groupingBy(Fractal::getSymbol));

        System.out.println();
    }

    @Async("taskExecutor")
    public void checkFractal(Candle candle) {
        List<Fractal> fractals = MAP_SYMBOL_FRACTALS.get(candle.getSymbol());

        if (CollectionUtils.isEmpty(fractals)) {
            return;
        }

        for (int i = fractals.size() - 1; i >= 0 && i > fractals.size() - 2; i--) {
            logicFractal(fractals.get(i), candle);
        }
    }

    public void logicFractal(Fractal fractal, Candle candle) {
        if ("WAXPUSDT".equals(fractal.getSymbol())) {
            System.out.println();
        }
        List<TelegramUser> traders = userService.findRoleByTrader();

        boolean isHigh = nonNull(fractal.getHigh());

        if (isHigh) {
            double highPriceFractal = Double.parseDouble(fractal.getHigh());
            double high = candle.getHigh();
            double change = (highPriceFractal - high) / high * 100;
            if (change < 1.5 && change >= 0) {
                String message = getInfoSymbol(candle.getSymbol());
                traders.forEach(r -> senderService.sendForUrl(r.getChatId(), message, "marker"));
            }
        } else {
            double lowPriceFractal = Double.parseDouble(fractal.getLow());
            double low = candle.getLow();
            double change = (low - lowPriceFractal) / lowPriceFractal * 100;
            if (change < 1.5 && change >= 0) {
                String message = getInfoSymbol(candle.getSymbol());
                traders.forEach(r -> senderService.sendForUrl(r.getChatId(), message, "marker"));
            }
        }
    }

    private String getInfoSymbol(String symbol) {
        String symBinance = symbol.replace("USDT", "_USDT");
        String symOkx = symbol.replace("USDT", "-USDT");
        String symBybit = symbol;
        symBinance = "[BINANCE](https://www.binance.com/ru/trade/" + symBinance + "?type=spot)\n";
        symOkx = "[OKX](https://www.okx.com/ru/trade-spot/" + symOkx + ")\n";
        symBybit = "[BYBIT](https://www.bybit.com/trade/usdt/" + symBybit + ")\n\n";

        return symbol + "цена подходит к фракталу 1d\n\n" + symBinance + symOkx + symBybit;
    }

    // Формула диапазона свечи в процентах
    // double change = (high - low) / low * 100
}
