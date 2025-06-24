package com.es.body.trading;

import com.es.body.trading.entity.Candle;
import org.json.JSONArray;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.es.body.statement.Utils.getResponse;
import static java.util.Objects.isNull;

public class CandleApi {

    public static List<Candle> getCandleWithoutTicks(String symbol, LocalDateTime startDateTime, String interval, Integer limit) {
//        String interval = "5m"; // 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
        long startTime = startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)
//        long startTime = LocalDateTime.parse("2025-05-30T05:15:00").toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)
//        long endTime = LocalDateTime.parse("2025-05-30T06:15:00").toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)

//        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d&limit=%d",
//                symbol,
//                interval, startTime, endTime, limit);
//        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d&limit=%d",
        String url;
        if (isNull(limit)) {
            url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d",
                    symbol,
                    interval, startTime);
        } else {
            url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&limit=%d",
                    symbol,
                    interval, limit);
        }

        String response = getResponse(url, 3);
        List<Candle> candels = new ArrayList<>(1000);
        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray candle = jsonArray.getJSONArray(i);
                    LocalDateTime dateTime = getDateTime(Long.parseLong(candle.get(0).toString()));
                    double volume = Double.parseDouble(candle.get(5).toString());

                    Candle c = Candle.builder()
                            .createDate(dateTime)
                            .open(Double.parseDouble(candle.get(1).toString()))
                            .high(Double.parseDouble(candle.get(2).toString()))
                            .low(Double.parseDouble(candle.get(3).toString()))
                            .close(Double.parseDouble(candle.get(4).toString()))
                            .vol(volume)
                            .interval(5)
                            .symbol(symbol)
                            .build();
                    candels.add(c);
                }
            } catch (Exception e) {
                System.out.println("Error parsing response: " + e.getMessage());
            }
        } else {
            System.out.println("No response from server");
        }
        return candels;
    }

    public static LocalDateTime getDateTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }
}

