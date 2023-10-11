package com.roy.server;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountDatabase {

    /*
        This is a DB
        1 => 10
        2 => 20
        ...
        10 => 100
     */

    private static final Map<Integer, Integer> MAP =
            IntStream.rangeClosed(1, 10)
                    .boxed()
                    .collect(Collectors.toMap(Function.identity(), value -> value * 10));

    public static int getBalance(int accountId) {
        return MAP.get(accountId);
    }

    public static Integer addBalance(int accountId, int amount) {
        return MAP.computeIfPresent(accountId, (key, value) -> value + amount);
    }

    public static Integer deductBalance(int accountId, int amount) {
        return MAP.computeIfPresent(accountId, (key, value) -> value - amount);
    }
}
