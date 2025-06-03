package com.payhub.pojo;

public enum DepositType {
    DEMAND("活期"),
    FIXED("定期");

    private final String description;

    DepositType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 