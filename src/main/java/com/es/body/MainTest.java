package com.es.body;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainTest {

//    public static void main(String[] args) {
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        Request request = new Request.Builder()
//                .url("https://enter.tochka.com/uapi/open-banking/v1.0/statements")
//                .method("GET", body)
//                .addHeader("Authorization", "Bearer <token>")
//                .build();
//        Response response = client.newCall(request).execute();
//    }

    public static void main() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            delay(1);
            System.out.println("I'm runnable - " + Thread.currentThread().getName());
        }, executorService);

        delay(2);
        System.out.println("Main - " + Thread.currentThread().getName());
        completableFuture.join();
    }

    public static void delay(int sleep) {
        try {
            TimeUnit.SECONDS.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<String> getUser() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("method getUser: " + Thread.currentThread().getName());
            delay(2);
            return "I'm Alexhabba";
        });
    }

    public static CompletableFuture<List<String>> getWishList(String user) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("method getWishList: " + Thread.currentThread().getName());
            delay(3);
            return List.of("Dress", "hello", "age");
        });
    }


    public static CompletableFuture<String> getUserEmail() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("method getUserEmail: " + Thread.currentThread().getName());
            delay(2);
            return "alexhabba@mail.ru";
        });
    }

    public static CompletableFuture<String> future1() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("method future1: " + Thread.currentThread().getName());
            delay(2);
            return "afuture1";
        });
    }

    public static CompletableFuture<String> future2() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("method future2: " + Thread.currentThread().getName());
            delay(2);
            throw new RuntimeException("pipec");
        });
    }

    public static CompletableFuture<String> future3() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("method future3: " + Thread.currentThread().getName());
            delay(1);
            return "afuture3";
        });
    }
}
