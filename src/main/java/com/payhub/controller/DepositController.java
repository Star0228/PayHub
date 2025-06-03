package com.payhub.controller;

import com.payhub.pojo.Deposit;
import com.payhub.pojo.DepositType;
import com.payhub.pojo.Result;
import com.payhub.service.DepositService;
import com.payhub.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/deposit")
public class DepositController {
    private static final Logger logger = LoggerFactory.getLogger(DepositController.class);

    @Autowired
    private DepositService depositService;
    
    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Result<Deposit>> createDeposit(@RequestBody CreateDepositRequest request) {
        List<String> errors = new ArrayList<>();
        
        // 验证用户ID
        if (request.getUserId() == null || request.getUserId() <= 0) {
            String error = "用户ID不能为空且必须大于0";
            errors.add(error);
            logger.error("创建存款失败: {}", error);
        } else {
            // 检查用户是否存在
            if (accountService.findByAccountId(request.getUserId()) == null) {
                String error = "用户不存在";
                errors.add(error);
                logger.error("创建存款失败: {}", error);
            }
        }
        
        // 验证存款类型
        if (request.getType() == null) {
            String error = "存款类型不能为空";
            errors.add(error);
            logger.error("创建存款失败: {}", error);
        }
        
        // 验证存款金额
        if (request.getAmount() == null) {
            String error = "存款金额不能为空";
            errors.add(error);
            logger.error("创建存款失败: {}", error);
        } else if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            String error = "存款金额必须大于0";
            errors.add(error);
            logger.error("创建存款失败: {}", error);
        } else if (request.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
            String error = "单笔存款金额不能超过100万";
            errors.add(error);
            logger.error("创建存款失败: {}", error);
        }
        
        // 如果有错误，返回错误信息
        if (!errors.isEmpty()) {
            String errorMsg = String.join("; ", errors);
            logger.error("创建存款失败: {}", errorMsg);
            return ResponseEntity.badRequest()
                    .body(Result.error(errorMsg));
        }
        
        try {
            Deposit deposit = depositService.createDeposit(request.getUserId(), request.getType(), request.getAmount());
            logger.info("创建存款成功: userId={}, type={}, amount={}, depositId={}", 
                    request.getUserId(), request.getType(), request.getAmount(), deposit.getId());
            return ResponseEntity.ok(Result.success(deposit, 1));
        } catch (Exception e) {
            logger.error("创建存款失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error("存款创建失败: " + e.getMessage()));
        }
    }

    @PostMapping("/can-withdraw")
    public ResponseEntity<Result<WithdrawCheckResult>> checkWithdrawable(@RequestBody WithdrawRequest request) {
        List<String> errors = new ArrayList<>();
        
        // 验证存款ID
        if (request.getDepositId() == null || request.getDepositId() <= 0) {
            String error = "存款ID不能为空且必须大于0";
            errors.add(error);
            logger.error("检查取款失败: {}", error);
            return ResponseEntity.badRequest()
                    .body(Result.error(String.join("; ", errors)));
        }
        
        try {
            boolean canWithdraw = depositService.canWithdraw(request.getDepositId());
            Deposit deposit = depositService.getDepositById(request.getDepositId());
            
            WithdrawCheckResult result = new WithdrawCheckResult();
            result.setCanWithdraw(canWithdraw);
            result.setDeposit(deposit);
            
            // 计算利息和总金额
            BigDecimal interest = depositService.calculateInterest(request.getDepositId());
            result.setInterest(interest);
            result.setTotalAmount(deposit.getAmount().add(interest));
            
            if (!canWithdraw) {
                if (deposit.getIsWithdrawn()) {
                    String reason = "该存款已经取款";
                    result.addReason(reason);
                    logger.warn("存款不能取款: depositId={}, reason={}", request.getDepositId(), reason);
                } else if (deposit.getType() == DepositType.FIXED) {
                    String reason = "定期存款未到期，不能取款";
                    result.addReason(reason);
                    logger.warn("存款不能取款: depositId={}, reason={}", request.getDepositId(), reason);
                }
            }
            
            logger.info("检查取款状态: depositId={}, canWithdraw={}, interest={}, totalAmount={}", 
                    request.getDepositId(), canWithdraw, interest, result.getTotalAmount());
            return ResponseEntity.ok(Result.success(result, 1));
        } catch (Exception e) {
            logger.error("检查取款失败: depositId={}, error={}", request.getDepositId(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error("取款检查失败: " + e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Result<WithdrawResult>> withdraw(@RequestBody WithdrawRequest request) {
        List<String> errors = new ArrayList<>();
        
        // 验证存款ID
        if (request.getDepositId() == null || request.getDepositId() <= 0) {
            String error = "存款ID不能为空且必须大于0";
            errors.add(error);
            logger.error("取款失败: {}", error);
            return ResponseEntity.badRequest()
                    .body(Result.error(String.join("; ", errors)));
        }
        
        try {
            BigDecimal amount = depositService.withdraw(request.getDepositId());
            Deposit deposit = depositService.getDepositById(request.getDepositId());
            
            WithdrawResult result = new WithdrawResult();
            result.setAmount(amount);
            result.setDeposit(deposit);
            
            logger.info("取款成功: depositId={}, amount={}, type={}", 
                    request.getDepositId(), amount, deposit.getType());
            return ResponseEntity.ok(Result.success(result, 1));
        } catch (IllegalStateException e) {
            logger.error("取款失败: depositId={}, error={}", request.getDepositId(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error("取款失败: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("取款失败: depositId={}, error={}", request.getDepositId(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error("取款失败: " + e.getMessage()));
        }
    }

    @PostMapping("/list")
    public ResponseEntity<Result<List<Deposit>>> getDeposits(@RequestBody DepositListRequest request) {
        List<String> errors = new ArrayList<>();
        
        // 验证账户ID
        if (request.getAccountId() == null || request.getAccountId() <= 0) {
            String error = "账户ID不能为空且必须大于0";
            errors.add(error);
            logger.error("查询存款记录失败: {}", error);
        } else {
            // 检查账户是否存在
            if (accountService.findByAccountId(request.getAccountId()) == null) {
                String error = "账户不存在";
                errors.add(error);
                logger.error("查询存款记录失败: {}", error);
            }
        }
        
        // 如果有错误，返回错误信息
        if (!errors.isEmpty()) {
            String errorMsg = String.join("; ", errors);
            logger.error("查询存款记录失败: {}", errorMsg);
            return ResponseEntity.badRequest()
                    .body(Result.error(errorMsg));
        }
        
        try {
            List<Deposit> deposits = depositService.getDepositsByAccountId(request.getAccountId());
            logger.info("查询存款记录成功: accountId={}, count={}", request.getAccountId(), deposits.size());
            return ResponseEntity.ok(Result.success(deposits, deposits.size()));
        } catch (Exception e) {
            logger.error("查询存款记录失败: accountId={}, error={}", request.getAccountId(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error("查询存款记录失败: " + e.getMessage()));
        }
    }
    
    // 内部类：取款请求
    public static class WithdrawRequest {
        private Long depositId;
        
        public Long getDepositId() {
            return depositId;
        }
        
        public void setDepositId(Long depositId) {
            this.depositId = depositId;
        }
    }
    
    // 内部类：查询存款记录请求
    public static class DepositListRequest {
        private Long accountId;
        
        public Long getAccountId() {
            return accountId;
        }
        
        public void setAccountId(Long accountId) {
            this.accountId = accountId;
        }
    }
    
    // 内部类：取款检查结果
    public static class WithdrawCheckResult {
        private boolean canWithdraw;
        private Deposit deposit;
        private List<String> reasons = new ArrayList<>();
        private BigDecimal interest;
        private BigDecimal totalAmount;
        
        public boolean isCanWithdraw() {
            return canWithdraw;
        }
        
        public void setCanWithdraw(boolean canWithdraw) {
            this.canWithdraw = canWithdraw;
        }
        
        public Deposit getDeposit() {
            return deposit;
        }
        
        public void setDeposit(Deposit deposit) {
            this.deposit = deposit;
        }
        
        public List<String> getReasons() {
            return reasons;
        }
        
        public void addReason(String reason) {
            this.reasons.add(reason);
        }

        public BigDecimal getInterest() {
            return interest;
        }

        public void setInterest(BigDecimal interest) {
            this.interest = interest;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
    
    // 内部类：取款结果
    public static class WithdrawResult {
        private BigDecimal amount;
        private Deposit deposit;
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public Deposit getDeposit() {
            return deposit;
        }
        
        public void setDeposit(Deposit deposit) {
            this.deposit = deposit;
        }
    }

    // 内部类：创建存款请求
    public static class CreateDepositRequest {
        private Long userId;
        private DepositType type;
        private BigDecimal amount;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public DepositType getType() {
            return type;
        }

        public void setType(DepositType type) {
            this.type = type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
} 