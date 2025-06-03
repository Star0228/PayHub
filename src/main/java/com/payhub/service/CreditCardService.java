package com.payhub.service;

import com.payhub.pojo.CreditCard;
import com.payhub.pojo.CreditCardTransaction;
import java.math.BigDecimal;
import java.util.List;

public interface CreditCardService {
    
    // 创建信用卡
    CreditCard createCreditCard(Long accountId);
    
    // 根据ID查询信用卡
    CreditCard getCreditCardById(Long id);
    
    // 根据卡号查询信用卡
    CreditCard getCreditCardByCardNumber(String cardNumber);
    
    // 查询用户的所有信用卡
    List<CreditCard> getCreditCardsByAccountId(Long accountId);
    
    // 挂失信用卡
    boolean reportLost(Long id, String reason);
    
    // 解挂信用卡
    boolean cancelLost(Long id);
    
    // 信用卡消费
    CreditCardTransaction consume(Long creditCardId, BigDecimal amount, String description);
    
    // 信用卡还款
    CreditCardTransaction repay(Long creditCardId, BigDecimal amount, String description);
    
    // 查询信用卡交易记录
    List<CreditCardTransaction> getTransactions(Long creditCardId);
    
    // 查询信用卡特定类型的交易记录
    List<CreditCardTransaction> getTransactionsByType(Long creditCardId, String type);
} 