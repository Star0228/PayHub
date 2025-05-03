package com.payhub.controller;

import com.payhub.pojo.Account;
import com.payhub.pojo.Result;
import com.payhub.service.AccountService;
import com.payhub.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping(value = "/register", produces = "application/json")
    public Result register(@RequestBody Account account) {
        System.out.println("Received registering account: " + account);
        String registerFeedback = accountService.register(account);
        
        if(registerFeedback.startsWith("注册成功")){
            // 从返回信息中提取accountId
            String accountIdStr = registerFeedback.substring(registerFeedback.indexOf(":")+1);
            Long accountId = Long.parseLong(accountIdStr);
            
            // 返回accountId给前端
            return Result.success(accountId, 1);
        } else {
            System.out.println(registerFeedback);
            return Result.error(registerFeedback);
        }
    }

    @PostMapping(value = "/login", produces = "application/json")
    public Result login(@RequestBody Account account) {
        System.out.println("Received logging account: " + account);
        Account loginAccount = null;
        
        // 优先通过accountId查找账户
        if(account.getAccountId() != null) {
            loginAccount = accountService.findByAccountId(account.getAccountId());
            if (loginAccount == null) {
                return Result.error("账户ID不存在");
            }
        } else {
            return Result.error("请提供账户ID");
        }

        // 验证username是否匹配
        if(account.getUsername() != null && !loginAccount.getUsername().equals(account.getUsername())) {
            return Result.error("用户名不匹配");
        }
        
        // 验证密码
        if(loginAccount.getPassword().equals(account.getPassword())) {
            System.out.println("Login success");

            // 生成 jwt 令牌
            Map<String,Object> claims = new HashMap<>();
            claims.put("id", loginAccount.getId());
            claims.put("accountId", loginAccount.getAccountId());
            claims.put("username", loginAccount.getUsername());
            String token = JwtUtil.genToken(claims);
            
            // 返回token和accountId
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("accountId", loginAccount.getAccountId());
            
            return Result.success(data, 3);
        } else {
            return Result.error("密码错误");
        }
    }

    @PostMapping(value = "/reset-password", produces = "application/json")
    public Result resetPassword(@RequestBody Account account) {
        System.out.println("Received reset password account: " + account);

        accountService.resetPasswordByUsername(account.getUsername(), account.getPassword());
        return Result.success();
    }
}