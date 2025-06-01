package com.payhub.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class LoanApplicationRecord {
    private String loanApplicationRecordId;
    private Long accountId;
    private BigDecimal amountOfMoney;
    private LocalDate applicationDate;
    private String loanExaminerId;
    private Integer creditScore;
    private String status; // 待审核, 通过, 拒绝
    private Integer term; // 贷款期限（月）
    private String purpose; // 贷款用途
    private Date createdAt;
    private Date updatedAt;
}
