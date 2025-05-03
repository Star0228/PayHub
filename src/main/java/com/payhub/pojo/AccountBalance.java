package com.payhub.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountBalance {
    private Long id;
    private Long accountId;
    private BigDecimal balance;
    private String currency;
    private Date updatedAt;
}