package com.payhub.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Deposit {
    private Long id;
    private Long accountId;
    private DepositType type;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private LocalDateTime maturityTime; // 定期存款到期时间
    private BigDecimal interestRate; // 利率
    private Boolean isWithdrawn; // 是否已取款
    private LocalDateTime withdrawTime; // 取款时间

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public DepositType getType() {
        return type;
    }

    public void setType(DepositType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getMaturityTime() {
        return maturityTime;
    }

    public void setMaturityTime(LocalDateTime maturityTime) {
        this.maturityTime = maturityTime;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Boolean getIsWithdrawn() {
        return isWithdrawn;
    }

    public void setIsWithdrawn(Boolean isWithdrawn) {
        this.isWithdrawn = isWithdrawn;
    }

    public LocalDateTime getWithdrawTime() {
        return withdrawTime;
    }

    public void setWithdrawTime(LocalDateTime withdrawTime) {
        this.withdrawTime = withdrawTime;
    }
} 