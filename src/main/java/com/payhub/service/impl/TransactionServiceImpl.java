package com.payhub.service.impl;

import com.payhub.mapper.AccountBalanceMapper;
import com.payhub.mapper.AccountMapper;
import com.payhub.mapper.TransactionRecordMapper;
import com.payhub.pojo.Account;
import com.payhub.pojo.AccountBalance;
import com.payhub.pojo.TransactionRecord;
import com.payhub.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountBalanceMapper accountBalanceMapper;

    @Autowired
    private TransactionRecordMapper transactionRecordMapper;

    @Autowired
    private AccountMapper accountMapper;

    // 转账事务
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public TransactionRecord transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String currency, String description) {
        // 参数校验
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("转账账户与收款账户不能相同");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("转账金额必须大于零");
        }

        Account fromAccount = accountMapper.selectByAccountId(fromAccountId);
        Account toAccount = accountMapper.selectByAccountId(toAccountId);
        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("账户不存在");
        }

        String transactionId = generateTransactionId();
        
        // 创建交易记录
        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(transactionId);
        record.setFromAccountId(fromAccountId);
        record.setToAccountId(toAccountId);
        record.setAmount(amount);
        record.setCurrency(currency);
        record.setStatus("PENDING");
        record.setType("TRANSFER");
        record.setDescription(description);
        
        transactionRecordMapper.insert(record);
        
        // 通过捕获异常进行回滚
        try {
            AccountBalance fromBalance = getAccountBalance(fromAccountId, currency);
            if (fromBalance == null || fromBalance.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("余额不足");
            }
            
            int decreaseResult = accountBalanceMapper.decreaseBalance(fromAccountId, amount, currency);
            if (decreaseResult == 0) {
                throw new IllegalStateException("余额不足或账户不存在");
            }
            
            AccountBalance toBalance = getAccountBalance(toAccountId, currency);
            if (toBalance == null) {
                // 如果收款账户没有对应币种的余额记录，则创建一个
                toBalance = new AccountBalance();
                toBalance.setAccountId(toAccountId);
                toBalance.setBalance(amount);
                toBalance.setCurrency(currency);
                accountBalanceMapper.insert(toBalance);
            } else {
                accountBalanceMapper.increaseBalance(toAccountId, amount, currency);
            }
            
            Date now = new Date();
            transactionRecordMapper.updateTransactionStatus(transactionId, "SUCCESS", now);
            record.setStatus("SUCCESS");
            record.setCompletedAt(now);
            
            return record;
        } catch (Exception e) {
            // 如果发生异常，更新交易状态为失败
            Date now = new Date();
            transactionRecordMapper.updateTransactionStatus(transactionId, "FAILED", now);
            record.setStatus("FAILED");
            record.setCompletedAt(now);
            
            // 抛出异常以触发事务回滚
            throw new RuntimeException("转账失败: " + e.getMessage(), e);
        }
    }


    @Override
    public AccountBalance getAccountBalance(Long accountId, String currency) {
        return accountBalanceMapper.selectByAccountIdAndCurrency(accountId, currency);
    }

    @Override
    public List<TransactionRecord> getTransactionRecords(Long accountId) {
        return transactionRecordMapper.selectByAccountId(accountId);
    }
    
    // uuid
    private String generateTransactionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}