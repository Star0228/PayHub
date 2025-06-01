package com.payhub.pojo;

import lombok.Data;

@Data
public class ForeignExchange {
    private String foreignExchangeId;
    private String currencyPair;
    private Float currentRate;
}
