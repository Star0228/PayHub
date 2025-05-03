package com.payhub.controller;

import com.payhub.pojo.AccountBalance;
import com.payhub.pojo.Result;
import com.payhub.pojo.TransactionRecord;
import com.payhub.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // 转账操作接口
    // 返回转账记录表中一条完整的记录
    @PostMapping("/transfer")
    public Result transfer(@RequestBody Map<String, Object> request) {
        try {
            // 请求参数
            Long fromAccountId = Long.parseLong(request.get("fromAccountId").toString());
            Long toAccountId = Long.parseLong(request.get("toAccountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = request.get("currency") != null ? request.get("currency").toString() : "CNY";
            String description = request.get("description") != null ? request.get("description").toString() : "";

            // 执行转账
            TransactionRecord record = transactionService.transfer(fromAccountId, toAccountId, amount, currency, description);

            return Result.success(record, 11);
        } catch (Exception e) {
            return Result.error("转账失败: " + e.getMessage());
        }
    }

    // 查余额
    @GetMapping("/balance/{accountId}")
    public Result getBalance(@PathVariable Long accountId, @RequestParam(required = false, defaultValue = "CNY") String currency) {
        try {
            AccountBalance balance = transactionService.getAccountBalance(accountId, currency);
            
            if (balance == null) {
                Map<String, Object> data = new HashMap<>();
                data.put("accountId", accountId);
                data.put("balance", BigDecimal.ZERO);
                data.put("currency", currency);
                return Result.success(data, 1);
            }
            
            return Result.success(balance, 1);
        } catch (Exception e) {
            return Result.error("查询余额失败: " + e.getMessage());
        }
    }

    // 查询转账记录
    @GetMapping("/records/{accountId}")
    public Result getTransactionRecords(@PathVariable Long accountId) {
        try {
            List<TransactionRecord> records = transactionService.getTransactionRecords(accountId);
            return Result.success(records, records.size());
        } catch (Exception e) {
            return Result.error("查询交易记录失败: " + e.getMessage());
        }
    }
}