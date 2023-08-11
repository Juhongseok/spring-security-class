package com.devcourse.springbootsecurityclass;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;

public class ThreadLocalApp {

    static ThreadLocal<Integer> threadLocalValue = new ThreadLocal<>();

    public static void main(String[] args) {
        System.out.println(getCurrentThreadName() + " ### main set value = 1");
        threadLocalValue.set(1);
        a();
        b();

        CompletableFuture<Void> task = runAsync(() -> {
            System.out.println(getCurrentThreadName() + " ### other thread set value = 2");
            threadLocalValue.set(2);
            a();
            b();
        });

        task.join();
    }

    public static void a() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + " ### a() get value = " + value);
    }

    public static void b() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + " ### b() get value = " + value);
    }

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }
}
