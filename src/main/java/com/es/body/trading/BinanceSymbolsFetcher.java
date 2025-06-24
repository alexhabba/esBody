package com.es.body.trading;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Получение информации по всем символам USDT spot.
 */
@Service
public class BinanceSymbolsFetcher {

    // Добавьте ObjectMapper как бин (или создайте один раз)
    private static final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public List<String> getAllSymbol(String quoteAsset) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
//                .url("https://api.binance.com/api/v3/exchangeInfo?symbol=SOPHUSDT")
                .url("https://api.binance.com/api/v3/exchangeInfo?permissions=SPOT")
                .header("User-Agent", "Mozilla/5.0")  // Обязательный заголовок!
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonNode root = mapper.readTree(response.body().byteStream());
                JsonNode symbols = root.get("symbols");

                List<String> currencies = new ArrayList<>();
                for (JsonNode symbol : symbols) {
                    if (symbol.get("quoteAsset").asText().equals(quoteAsset)
                            && symbol.get("status").asText().equals("TRADING")) {
                        currencies.add(symbol.get("symbol").asText());
                    }
                }
                return currencies;
            }
        }
        return List.of();
    }
}
