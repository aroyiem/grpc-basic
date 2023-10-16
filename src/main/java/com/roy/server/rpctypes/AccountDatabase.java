package com.roy.server.rpctypes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountDatabase {

    /*
        This is a DB
        1 => 100
        2 => 200
        ...
        10 => 1000
     */

    private static final Map<Integer, Integer> MAP =
            IntStream.rangeClosed(1, 10)
                    .boxed()
                    .collect(Collectors.toMap(Function.identity(), value -> value * 100));

    public static int getBalance(int accountId) {
        return MAP.get(accountId);
    }

    public static Integer addBalance(int accountId, int amount) {
        return MAP.computeIfPresent(accountId, (key, value) -> value + amount);
    }

    public static Integer deductBalance(int accountId, int amount) {
        return MAP.computeIfPresent(accountId, (key, value) -> value - amount);
    }

    public static void printAccountDetails() {
        System.out.println(MAP);
    }
}
