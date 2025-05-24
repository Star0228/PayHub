package com.payhub.utils;

public class UserContext {
    private static final ThreadLocal<Long> accountIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<Integer> userFlagHolder = new ThreadLocal<>();

    public static void setAccountId(Long accountId) {
        accountIdHolder.set(accountId);
    }

    public static Long getAccountId() {
        return accountIdHolder.get();
    }

    public static void setUserFlag(Integer userFlag) {
        userFlagHolder.set(userFlag);
    }

    public static Integer getUserFlag() {
        return userFlagHolder.get();
    }

    public static void clear() {
        accountIdHolder.remove();
        userFlagHolder.remove();
    }
}
