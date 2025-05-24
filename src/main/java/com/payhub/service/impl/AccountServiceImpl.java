package com.payhub.service.impl;

import com.payhub.mapper.AccountMapper;
import com.payhub.pojo.Account;
import com.payhub.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    // 初始账户ID值
    private static final long INITIAL_ACCOUNT_ID = 1000000L;

    @Override
    public Account findByUsername(String username) {
        return accountMapper.selectByUsername(username);
    }

    @Override
    public Account findByAccountId(Long accountId) {
        return accountMapper.selectByAccountId(accountId);
    }

    @Override
    public String register(Account account) {
        // 验证账户信息
        if (account.getUsername() == null || account.getUsername().length() < 6) {
            return "用户名必须至少6个字符";
        }
        
        if (account.getEmail() == null || !account.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "邮箱格式不正确";
        }
        
        // 检查用户名和邮箱唯一性
        if (accountMapper.selectByUsername(account.getUsername()) != null) {
            return "用户名已存在";
        }
        
        if (accountMapper.selectByEmail(account.getEmail()) != null) {
            return "邮箱已存在";
        }

        // 为新用户生成唯一ID和账户ID
        account.setId(generateUniqueId());
        Long accountId = generateAccountId();
        account.setAccountId(accountId);
        // 设置用户类型，默认普通用户
        if (account.getUserFlag() == null) {
            account.setUserFlag(1); // 默认普通用户
        }
        
        // 如果其他字段为空，设置默认值
        if (account.getCardId() == null) {
            account.setCardId(0L);
        }
        
        accountMapper.insert(account);
        // 返回成功消息，包含accountId
        return "注册成功！accountId:" + accountId;
    }

    @Override
    public void resetPasswordByUsername(String username, String password) {
        accountMapper.updatePasswordByUsername(username, password);
    }
    
    // 类似雪花算法，根据时间生成唯一ID
    private Long generateUniqueId() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    // 生成账户ID，从1000000开始自增
    private Long generateAccountId() {
        Long maxAccountId = accountMapper.getMaxAccountId();
        if (maxAccountId == null) {
            return INITIAL_ACCOUNT_ID;
        } else {
            return maxAccountId + 1;
        }
    }
}