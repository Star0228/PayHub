package com.payhub.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class Loan {
    private String loanId;
    private String loanApplicationRecordId;
    private BigDecimal amountOfMoney;
    private LocalDate repaymentDate;
    private Float interestRate;
    private Boolean isOverdue;
    private String status; // 放款中, 还款中, 已结清, 已逾期
    private Date createdAt;
    private Date updatedAt;
}
