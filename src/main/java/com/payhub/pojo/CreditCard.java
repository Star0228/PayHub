package com.payhub.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditCard {
    private Long id;
    private Long accountId;
    private String cardNumber;
    private Boolean isLost;
    private BigDecimal creditLimit;
    private BigDecimal availableCredit;
    private LocalDateTime lostTime;
    private String lostReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 