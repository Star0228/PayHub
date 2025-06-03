package com.payhub.service.impl;

import com.payhub.mapper.CreditCardMapper;
import com.payhub.mapper.CreditCardTransactionMapper;
import com.payhub.mapper.AccountMapper;
import com.payhub.pojo.CreditCard;
import com.payhub.pojo.CreditCardTransaction;
import com.payhub.pojo.Account;
import com.payhub.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private CreditCardMapper creditCardMapper;
    
    @Autowired
    private CreditCardTransactionMapper transactionMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public CreditCard getCreditCardById(Long id) {
        return creditCardMapper.findById(id);
    }

    @Override
    public CreditCard getCreditCardByCardNumber(String cardNumber) {
        return creditCardMapper.findByCardNumber(cardNumber);
    }

    @Override
    public List<CreditCard> getCreditCardsByAccountId(Long accountId) {
        return creditCardMapper.findByAccountId(accountId);
    }

    @Override
    @Transactional
    public boolean reportLost(Long id, String reason) {
        CreditCard creditCard = creditCardMapper.findById(id);
        if (creditCard == null) {
            throw new IllegalArgumentException("信用卡不存在");
        }

        if (creditCard.getIsLost()) {
            throw new IllegalStateException("信用卡已经处于挂失状态");
        }

        return creditCardMapper.updateLostStatus(id, true, LocalDateTime.now(), reason) > 0;
    }

    @Override
    @Transactional
    public boolean cancelLost(Long id) {
        CreditCard creditCard = creditCardMapper.findById(id);
        if (creditCard == null) {
            throw new IllegalArgumentException("信用卡不存在");
        }

        if (!creditCard.getIsLost()) {
            throw new IllegalStateException("信用卡未处于挂失状态");
        }

        return creditCardMapper.updateLostStatus(id, false, null, null) > 0;
    }
    
    @Override
    @Transactional
    public CreditCardTransaction consume(Long creditCardId, BigDecimal amount, String description) {
        CreditCard creditCard = creditCardMapper.findById(creditCardId);
        if (creditCard == null) {
            throw new IllegalArgumentException("信用卡不存在");
        }
        
        if (creditCard.getIsLost()) {
            throw new IllegalStateException("信用卡已挂失，无法消费");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("消费金额必须大于0");
        }
        
        if (amount.compareTo(creditCard.getAvailableCredit()) > 0) {
            throw new IllegalStateException("可用额度不足");
        }
        
        // 更新可用额度
        creditCard.setAvailableCredit(creditCard.getAvailableCredit().subtract(amount));
        creditCardMapper.updateAvailableCredit(creditCardId, creditCard.getAvailableCredit());
        
        // 创建交易记录
        CreditCardTransaction transaction = new CreditCardTransaction();
        transaction.setCreditCardId(creditCardId);
        transaction.setAccountId(creditCard.getAccountId());
        transaction.setType("CONSUME");
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionTime(LocalDateTime.now());
        
        transactionMapper.insert(transaction);
        return transaction;
    }
    
    @Override
    @Transactional
    public CreditCardTransaction repay(Long creditCardId, BigDecimal amount, String description) {
        CreditCard creditCard = creditCardMapper.findById(creditCardId);
        if (creditCard == null) {
            throw new IllegalArgumentException("信用卡不存在");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("还款金额必须大于0");
        }
        
        // 更新可用额度
        creditCard.setAvailableCredit(creditCard.getAvailableCredit().add(amount));
        creditCardMapper.updateAvailableCredit(creditCardId, creditCard.getAvailableCredit());
        
        // 创建交易记录
        CreditCardTransaction transaction = new CreditCardTransaction();
        transaction.setCreditCardId(creditCardId);
        transaction.setAccountId(creditCard.getAccountId());
        transaction.setType("REPAYMENT");
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionTime(LocalDateTime.now());
        
        transactionMapper.insert(transaction);
        return transaction;
    }
    
    @Override
    public List<CreditCardTransaction> getTransactions(Long creditCardId) {
        return transactionMapper.findByCreditCardId(creditCardId);
    }
    
    @Override
    public List<CreditCardTransaction> getTransactionsByType(Long creditCardId, String type) {
        return transactionMapper.findByCreditCardIdAndType(creditCardId, type);
    }

    @Override
    @Transactional
    public CreditCard createCreditCard(Long accountId) {
        // 检查用户是否存在
        Account account = accountMapper.selectByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 生成卡号（16位）
        String cardNumber = generateCardNumber();
        
        // 设置初始信用额度（根据用户年收入设置）
        BigDecimal creditLimit = calculateCreditLimit(account.getAnnualIncome());
        
        // 创建信用卡
        CreditCard creditCard = new CreditCard();
        creditCard.setAccountId(accountId);
        creditCard.setCardNumber(cardNumber);
        creditCard.setIsLost(false);
        creditCard.setCreditLimit(creditLimit);
        creditCard.setAvailableCredit(creditLimit);
        creditCard.setCreatedAt(LocalDateTime.now());
        creditCard.setUpdatedAt(LocalDateTime.now());
        
        creditCardMapper.insert(creditCard);
        return creditCard;
    }

    // 生成16位卡号
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    // 根据年收入计算信用额度
    private BigDecimal calculateCreditLimit(Double annualIncome) {
        if (annualIncome == null || annualIncome <= 0) {
            return new BigDecimal("10000"); // 默认额度
        }
        // 信用额度为年收入的20%，最低10000，最高100000
        BigDecimal limit = new BigDecimal(annualIncome).multiply(new BigDecimal("0.2"));
        if (limit.compareTo(new BigDecimal("10000")) < 0) {
            return new BigDecimal("10000");
        }
        if (limit.compareTo(new BigDecimal("100000")) > 0) {
            return new BigDecimal("100000");
        }
        return limit;
    }
} 