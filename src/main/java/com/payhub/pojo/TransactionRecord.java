package com.payhub.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionRecord {
    private Long id;
    private String transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String currency;
    private String status;  // 事务状态： PENDING, SUCCESS, FAILED
    private String type;  
    private String description;
    private Date createdAt;
    private Date completedAt;
}