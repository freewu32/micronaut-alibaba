package com.github.freewu32.nacos.utils;

public class Assert {
    public static void isTrue(boolean result, String message) {
        if (!result) {
            throw new RuntimeException(message);
        }
    }
}
