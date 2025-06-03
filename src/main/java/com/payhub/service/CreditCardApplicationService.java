package com.payhub.service;

import com.payhub.pojo.CreditCardApplication;
import java.util.List;

public interface CreditCardApplicationService {
    
    // 创建信用卡申请
    CreditCardApplication createApplication(Long accountId);
    
    // 根据ID查询申请
    CreditCardApplication getApplicationById(Long id);
    
    // 查询用户的所有申请
    List<CreditCardApplication> getApplicationsByAccountId(Long accountId);
    
    // 查询特定状态的所有申请
    List<CreditCardApplication> getApplicationsByStatus(String status);
    
    // 更新申请状态
    boolean updateApplicationStatus(Long id, String status, Long reviewerId);

    // 查询当前用户的所有申请记录
    List<CreditCardApplication> getAllApplicationsByCurrentUser(Long accountId);
} 