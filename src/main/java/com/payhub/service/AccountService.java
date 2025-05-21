package com.payhub.service;

import com.payhub.pojo.Account;

public interface AccountService {

    String register(Account account);

    Account findByUsername(String username);
    
    Account findByAccountId(Long accountId);

    void resetPasswordByUsername(String username, String password);
}