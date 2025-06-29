package com.es.body.trading;

import com.es.body.entity.TelegramUser;
import com.es.body.service.SenderService;
import com.es.body.service.UserService;
import com.es.body.trading.entity.Candle;
import com.es.body.trading.repository.CandleRepository;
import com.es.body.trading.service.FractalD1Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.es.body.trading.CandleApi.getCandleWithoutTicks;

@Service
@RequiredArgsConstructor
public class CandleService {

    private final FractalD1Notification fractalD1Notification;
    private final UserService userService;
    private final BinanceSymbolsFetcher binanceSymbolsFetcher;
    private final SenderService senderService;

    // WIFUSDT 2025-06-10T16:55
    private static final Set<String> SET_SYMBOL_LOCAL_DATE_TIME = new HashSet<>();

    private static final Map<LocalDateTime, Set<String>> MAP_LOCAL_DATE_TIME_LIST_SYMBOL = new HashMap<>();

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
            "USDPUSDT",
            "BNBUSDT",
            "SOLUSDT",
            "BTCUSDT",
            "BTTCUSDT",
            "TUSDUSDT",
            "WBTCUSDT"
    );

    private static final Map<String, Function<Double, Integer>> REZOLVER = Map.of(
            BTC, CandleService::getVolBtc,
            USDT, CandleService::getVolUsdt
    );

    private final List<String> symbols = new ArrayList<>();

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        List<String> usdt = binanceSymbolsFetcher.getAllSymbol("USDT");
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

                    List<Candle> candleLIst = getCandleWithoutTicks(s, LocalDateTime.now().minusHours(3).minusMinutes(minutes), interval, null);
//                    List<Candle> candles = candleLIst.stream().limit(medianCount).collect(Collectors.toList());

//                    fractalD1Notification.checkFractal(candleLIst.get(candleLIst.size() - 1));

                    if (BTC.concat(USDT).equals(s)) {
                        btcPrice = candleLIst.get(candleLIst.size() - 1).getClose();
                    }

                    // нужно не считать последние 3 свечи
                    double medianVolume = candleLIst.stream()
                            .mapToDouble(Candle::getVol)
                            .sum() / candleLIst.size() * 3;

                    Map<Double, LocalDateTime> mapVolLocalDateTime = new HashMap<>();
//                    double vol0 = candleLIst.get(candleLIst.size() - 1).getVol();
//                    mapVolLocalDateTime.put(vol0, candleLIst.get(candleLIst.size() - 1).getCreateDate());
                    Candle candle = candleLIst.get(candleLIst.size() - 2);
                    double vol1 = candle.getVol();
                    mapVolLocalDateTime.put(vol1, candle.getCreateDate());
//                    double vol2 = candleLIst.get(candleLIst.size() - 3).getVol();
//                    mapVolLocalDateTime.put(vol2, candleLIst.get(candleLIst.size() - 3).getCreateDate());

                    double pr0 = candle.getClose();
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

                    // Формула диапазона свечи в процентах
                    // double change = (high - low) / low * 100
                    double change = (candle.getHigh() - candle.getLow()) / candle.getLow() * 100;

//                    if ((candleLIst.size() >= medianCount && checkSum && medianVolume < vol1) || change > 3) {
                    // тестируем пока изменения больше чем на 3 процента
                    if (change > 3) {
                        List<TelegramUser> allByRoles = userService.findRoleByTrader();
                        if (!allByRoles.isEmpty()) {
                            Set<String> symbolsList = MAP_LOCAL_DATE_TIME_LIST_SYMBOL.computeIfAbsent(
                                    candle.getCreateDate(),
                                    k -> new HashSet<>()
                            );

                            if (symbolsList.add(candle.getSymbol())) {
                                String message = getInfoSymbol(s, maxVolInUsdt, forMaxVol, change, candle.getClose() > candle.getOpen());

                                allByRoles.forEach(r -> senderService.sendForUrl(r.getChatId(), message, "marker"));
                            }
                        }
                    }

//                    2025-06-10T16:05  -> 2025-06-10T16:35  2025-06-10T16:36
                    cleanRow(candle);

                });
    }

    private static void cleanRow(Candle candle) {
        Set<LocalDateTime> setFilteredForDeleteList = MAP_LOCAL_DATE_TIME_LIST_SYMBOL.keySet().stream()
                .filter(dateTime -> dateTime.plusMinutes(30).isBefore(candle.getCreateDate()))
                .collect(Collectors.toSet());

        setFilteredForDeleteList.forEach(dateTime -> {
            System.out.println("deleted row: " + MAP_LOCAL_DATE_TIME_LIST_SYMBOL.remove(dateTime).size());
        });
    }

    private static Integer getVolBtc(double maxVol) {
        return (int) (maxVol * btcPrice);
    }

    private static Integer getVolUsdt(double maxVol) {
        return (int) (maxVol * 1);
    }


    // todo нужно добавить индикатор роста/падения
    private String getInfoSymbol(String symbol, int maxVolInUsdt, LocalDateTime forMaxVol, double change, boolean isGreen) {
        String symBinance = symbol.replace("USDT", "_USDT");
        String symOkx = symbol.replace("USDT", "-USDT");
        String symBybit = symbol;
        symBinance = "[BINANCE](https://www.binance.com/ru/trade/" + symBinance + "?type=spot)\n";
        symOkx = "[OKX](https://www.okx.com/ru/trade-spot/" + symOkx + ")\n";
        symBybit = "[BYBIT](https://www.bybit.com/trade/usdt/" + symBybit + ")\n\n";
        String changeStr = "change: " + roundWithDecimalFormat(change) + " %\n\n";
        String maxVol = "maxVolInUsdt: " + formatWithSpaces(maxVolInUsdt) + "\n\n";
        String dateTime = "dateTime: " + forMaxVol + "\n\n";
        String indicator = isGreen ? "\uD83D\uDFE2 " : "\uD83D\uDD34 ";
        return  indicator + symbol + "\n\n" + symBinance + symOkx + symBybit + changeStr + maxVol + dateTime;
    }

    public static double roundWithDecimalFormat(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }

    public static String formatWithSpaces(long number) {
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        formatter.setGroupingSize(3);
        formatter.setGroupingUsed(true);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' '); // Устанавливаем пробел как разделитель
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }
// Формула диапазона свечи в процентах
// double change = (high - low) / low * 100 %
}
