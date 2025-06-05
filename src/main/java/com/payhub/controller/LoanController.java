package com.payhub.controller;

import com.payhub.service.LoanService;
import com.payhub.pojo.LoanApplicationRecord;
import com.payhub.pojo.Loan;
import com.payhub.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin
public class LoanController {
    
    @Autowired
    private LoanService loanService;
    
    /**
     * 贷款申请
     */
    @PostMapping("/apply")
    public Result<Map<String, Object>> applyLoan(@RequestBody LoanApplicationRecord application) {
        return loanService.applyLoan(application);
    }
    
    /**
     * 审批贷款
     */
    @PostMapping("/approve")
    public Result<Map<String, Object>> approveLoan(@RequestBody Map<String, Object> request) {
        String applicationId = (String) request.get("applicationId");
        String examinerId = (String) request.get("examinerId");
        String result = (String) request.get("result");
        return loanService.approveLoan(applicationId, examinerId, result);
    }
    
    /**
     * 贷款还款
     */
    @PostMapping("/repay")
    public Result<Map<String, Object>> repayLoan(@RequestBody Map<String, Object> request) {
        String loanId = (String) request.get("loanId");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        return loanService.repayLoan(loanId, amount);
    }
      /**
     * 查询用户的贷款申请
     */    @GetMapping("/applications/{accountId}")
    public Result<List<LoanApplicationRecord>> getLoanApplications(@PathVariable Long accountId) {
        return loanService.getLoanApplications(accountId);
    }
    
    /**
     * 查询用户的贷款记录
     */    @GetMapping("/loans/{accountId}")
    public Result<List<Loan>> getLoans(@PathVariable Long accountId) {
        return loanService.getLoans(accountId);
    }
    
    /**
     * 手动触发逾期检查（管理员功能）
     */
    @PostMapping("/check-overdue")
    public Result<String> checkOverdueLoans() {        try {
            loanService.checkOverdueLoans();
            return Result.success("逾期检查完成", 1);
        } catch (Exception e) {
            return Result.error("逾期检查失败: " + e.getMessage());
        }
    }
}
