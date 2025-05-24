package com.payhub.service;

import com.payhub.pojo.AccountBalance;
import com.payhub.pojo.TransactionRecord;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    
    // 转账
    TransactionRecord transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String currency, String description);
    
    // 查余额
    AccountBalance getAccountBalance(Long accountId, String currency);
    
    // 查交易记录
    List<TransactionRecord> getTransactionRecords(Long accountId);

    // 管理员：查所有交易记录
    List<TransactionRecord> getAllTransactionRecords();

    // 管理员：按时间范围查交易记录
    List<TransactionRecord> getTransactionRecordsByTimeRange(String startTime, String endTime);
}