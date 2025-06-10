package com.es.body.trading;

import com.es.body.entity.TelegramUser;
import com.es.body.enums.Role;
import com.es.body.service.SenderService;
import com.es.body.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.es.body.trading.CandleApi.getCandleWithoutTicks;

@Service
@RequiredArgsConstructor
public class CandleService {

    private final UserService userService;
    private final BinanceSymbolsFetcher binanceSymbolsFetcher;
    private final SenderService senderService;

    private static final int medianCountTen = 10;
    private static final int medianCountTwenty = 20;
    private static double btcPrice;
    private static final String BTC = "BTC";
    private static final String USDT = "USDT";

    private static final Map<Integer, Map<String, Integer>> map = Map.of(
            medianCountTen, Map.of(
                    "1d", (medianCountTen + 1) * 24 * 60,
                    "5m", (medianCountTen + 1) * 5
            ),
            medianCountTwenty, Map.of(
                    "1d", (medianCountTwenty + 1) * 24 * 60,
                    "5m", (medianCountTwenty + 1) * 5
            )
    );

    private static final Set<String> setSymbolExcludes = Set.of(
            "FDUSDUSDT",
            "USDCUSDT",
            "WBTCBTC",
            "XUSDUSDT",
            "TRXUSDT",
            "ETHBTC ",
            "ETHUSDT",
            "LTCUSDT",
            "XRPUSDT",
            "WBTCUSDT"
    );

    private static final Map<String, Function<Double, Integer>> REZOLVER = Map.of(
            BTC, CandleService::getVolBtc,
            USDT, CandleService::getVolUsdt
    );

    private final List<String> symbols = new ArrayList<>();

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        List<String> usdt = binanceSymbolsFetcher.getAllSymboll("USDT");
        symbols.addAll(usdt);
    }

    public void run() {
        run(symbols, 10, "5m");
    }

    private void run(List<String> symbols, int medianCount, String interval) {
        Integer minutes = map.get(medianCount).get(interval);
        symbols.stream()
                .filter(sym -> !setSymbolExcludes.contains(sym))
                .forEach(s -> {

                    List<Candle> candle = getCandleWithoutTicks(s, LocalDateTime.now().minusHours(3).minusMinutes(minutes), interval);
                    List<Candle> candles = candle.stream().limit(medianCount).collect(Collectors.toList());

                    if (BTC.concat(USDT).equals(s)) {
                        btcPrice = candle.get(candle.size() - 1).getClose();
                    }

                    // нужно не считать последние 3 свечи
                    double medianVolume = candles.stream()
                            .mapToDouble(Candle::getVol)
                            .sum() / candles.size() * 2;

                    Map<Double, LocalDateTime> mapVolLocalDateTime = new HashMap<>();
//                    double vol0 = candle.get(candle.size() - 1).getVol();
//                    mapVolLocalDateTime.put(vol0, candle.get(candle.size() - 1).getCreateDate());
                    double vol1 = candle.get(candle.size() - 2).getVol();
                    mapVolLocalDateTime.put(vol1, candle.get(candle.size() - 2).getCreateDate());
//                    double vol2 = candle.get(candle.size() - 3).getVol();
//                    mapVolLocalDateTime.put(vol2, candle.get(candle.size() - 3).getCreateDate());

                    double pr0 = candle.get(candle.size() - 2).getClose();
                    double maxVolUsdt = vol1 * pr0;
                    double maxVol = vol1;
                    LocalDateTime forMaxVol = mapVolLocalDateTime.get(maxVol);
                    int maxVolInUsdt;
                    if (s.endsWith(BTC)) {
                        maxVolInUsdt = REZOLVER.get(BTC).apply(maxVolUsdt);
                    } else if (s.endsWith(USDT)) {
                        maxVolInUsdt = REZOLVER.get(USDT).apply(maxVolUsdt);
                    } else {
                        maxVolInUsdt = 0;
                    }
                    int bigVol = 1000000;
                    boolean checkSum = maxVolInUsdt > bigVol;

                    if (candle.size() >= medianCount && checkSum && medianVolume < vol1) {
                        List<TelegramUser> allByRoles = userService.findRoleByTrader();
                        if (!allByRoles.isEmpty()) {
                            allByRoles.forEach(r -> senderService.send(r.getChatId(), getInfoSymbol(s, maxVolInUsdt, forMaxVol)));
                        }
                    }
                });
    }

    private static Integer getVolBtc(double maxVol) {
        return (int) (maxVol * btcPrice);
    }

    private static Integer getVolUsdt(double maxVol) {
        return (int) (maxVol * 1);
    }

    private String getInfoSymbol(String symbol, int maxVolInUsdt, LocalDateTime forMaxVol) {
        String symBinance = symbol.replace("USDT", "_USDT");
        String symOkx = symbol.replace("USDT", "-USDT");
        String symBybit = symbol;
        symBinance = "[BINANCE](https://www.binance.com/ru/trade/" + symBinance + "?type=spot)\n";
        symOkx = "[OKX](https://www.okx.com/ru/trade-spot/" + symOkx + ")\n";
        symBybit = "[BYBIT](https://www.bybit.com/trade/usdt/" + symBybit + ")\n\n";
        String maxVol = "maxVolInUsdt: " + maxVolInUsdt + "\n";
        String dateTime = "dateTime: " + forMaxVol + "\n\n";

        return symbol + "\n" + symBinance + symOkx + symBybit + maxVol + dateTime;
    }

}
