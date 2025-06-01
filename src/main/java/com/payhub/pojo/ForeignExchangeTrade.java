package com.payhub.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ForeignExchangeTrade {
    private String foreignExchangeTradeId;
    private Long accountId;
    private String foreignExchangeId;
    private String currencyPair;
    private BigDecimal amount;
    private String tradeType; // BUY, SELL
    private String status; // SUCCESS, FAILED
    private LocalDateTime tradeTime;
    private BigDecimal exchangedAmount; // 兑换后的金额
    private Float exchangeRate; // 交易时的汇率
}
