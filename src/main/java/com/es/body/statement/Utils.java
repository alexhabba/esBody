package com.es.body.statement;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.SocketTimeoutException;

public class Utils {

    private static final OkHttpClient client = new OkHttpClient();

    @SneakyThrows
    public static String getResponse(String url, int count) {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")  // Обязательный заголовок!
                .build();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                if (response.code() == 429) {
                    System.err.println("GET request failed with code " + response.code());

                    Thread.sleep(60000);
                } else if (response.code() == 418) {
                    System.err.println("GET request failed with code " + response.code());
                    Thread.sleep(60000 * 5);
                }
                System.err.println("GET request failed with code " + response.code());
                return null;
            }
        } catch (SocketTimeoutException ex) {
            if (count == 0) {
                System.out.println("не удалось отправить запрос за 3 попытки: \n" + url);
            } else {
                Thread.sleep(10000);
                return getResponse(url, --count);
            }
        }
        return null;
    }

    @SneakyThrows
    public static String getResponse(Request request, int count) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                if (response.code() == 429) {
                    System.err.println("GET request failed with code " + response.code());

                    Thread.sleep(60000);
                } else if (response.code() == 418) {
                    System.err.println("GET request failed with code " + response.code());
                    Thread.sleep(60000 * 5);
                }
                System.err.println("GET request failed with code " + response.code());
                return null;
            }
        } catch (SocketTimeoutException ex) {
            if (count == 0) {
                System.out.println("не удалось отправить запрос за 3 попытки: \n" + request.url());
            } else {
                Thread.sleep(10000);
                return getResponse(request, --count);
            }
        }
        return null;
    }

    @SneakyThrows
    public static String postResponse(Request request, int count) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                if (response.code() == 429) {
                    System.err.println("GET request failed with code " + response.code());

                    Thread.sleep(60000);
                } else if (response.code() == 418) {
                    System.err.println("GET request failed with code " + response.code());
                    Thread.sleep(60000 * 5);
                }
                System.err.println("GET request failed with code " + response.code());
                return null;
            }
        } catch (SocketTimeoutException ex) {
            if (count == 0) {
                System.out.println("не удалось отправить запрос за 3 попытки: \n");
            } else {
                Thread.sleep(10000);
                return postResponse(request, --count);
            }
        }
        return null;
    }
}
