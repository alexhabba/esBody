package com.logicaScoolBot;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainTest {

    public static void main(String[] args) {
//        CompletableFuture<List<String>> listCompletableFuture = getUser().thenCompose(MainTest::getWishList);
//
//        List<String> join = listCompletableFuture.join();
//
//        System.out.println(join);
//
//
//        CompletableFuture<QrStatus> stringCompletableFuture = getUserEmail()
//                .thenCombine(getUser(), (e, u) -> {
//                    System.out.println("thenCombine" + Thread.currentThread().getName());
//                    return QrStatus.Accepted;
//                });
//
//        System.out.println(stringCompletableFuture.join());

        CompletableFuture<Object> voidCompletableFuture = CompletableFuture.anyOf(future1(), future2(), future3());

        System.out.println(voidCompletableFuture.join());
    }

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
