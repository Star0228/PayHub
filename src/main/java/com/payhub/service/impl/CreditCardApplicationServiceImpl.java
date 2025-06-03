package com.payhub.service.impl;

import com.payhub.mapper.CreditCardApplicationMapper;
import com.payhub.mapper.AccountMapper;
import com.payhub.pojo.CreditCardApplication;
import com.payhub.pojo.Account;
import com.payhub.service.CreditCardApplicationService;
import com.payhub.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreditCardApplicationServiceImpl implements CreditCardApplicationService {

    @Autowired
    private CreditCardApplicationMapper applicationMapper;
    
    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private CreditCardService creditCardService;

    @Override
    @Transactional
    public CreditCardApplication createApplication(Long accountId) {
        // 检查用户是否存在
        Account account = accountMapper.selectByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查用户是否已有待审核的申请
        List<CreditCardApplication> pendingApplications = applicationMapper.findByAccountId(accountId);
        for (CreditCardApplication app : pendingApplications) {
            if ("PENDING".equals(app.getStatus())) {
                throw new IllegalStateException("您已有一个待审核的申请");
            }
        }

        // 创建新申请
        CreditCardApplication application = new CreditCardApplication();
        application.setAccountId(accountId);
        application.setStatus("PENDING");
        application.setApplicationDate(LocalDateTime.now());
        
        applicationMapper.insert(application);
        return application;
    }

    @Override
    public CreditCardApplication getApplicationById(Long id) {
        return applicationMapper.findById(id);
    }

    @Override
    public List<CreditCardApplication> getApplicationsByAccountId(Long accountId) {
        return applicationMapper.findByAccountId(accountId);
    }

    @Override
    public List<CreditCardApplication> getApplicationsByStatus(String status) {
        return applicationMapper.findByStatus(status);
    }

    @Override
    @Transactional
    public boolean updateApplicationStatus(Long id, String status, Long reviewerId) {
        CreditCardApplication application = applicationMapper.findById(id);
        if (application == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }

        if (!"PENDING".equals(application.getStatus())) {
            throw new IllegalStateException("只能更新待审核的申请");
        }

        boolean result = applicationMapper.updateStatus(id, status, reviewerId, LocalDateTime.now()) > 0;
        
        // 如果审核通过，创建信用卡
        if (result && "APPROVED".equals(status)) {
            try {
                creditCardService.createCreditCard(application.getAccountId());
            } catch (Exception e) {
                throw new RuntimeException("创建信用卡失败：" + e.getMessage());
            }
        }

        return result;
    }

    @Override
    public List<CreditCardApplication> getAllApplicationsByCurrentUser(Long accountId) {
        // 检查用户是否存在
        Account account = accountMapper.selectByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 获取该用户的所有申请记录
        return applicationMapper.findByAccountId(accountId);
    }
} 