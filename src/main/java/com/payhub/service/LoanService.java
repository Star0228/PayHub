package com.payhub.service;

import com.payhub.mapper.LoanMapper;
import com.payhub.pojo.LoanApplicationRecord;
import com.payhub.pojo.Loan;
import com.payhub.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LoanService {
    
    @Autowired
    private LoanMapper loanMapper;
    
    /**
     * 申请贷款
     */
    @Transactional
    public Result<Map<String, Object>> applyLoan(LoanApplicationRecord application) {
        try {
            // 参数校验
            if (application.getAmountOfMoney() == null || application.getAmountOfMoney().compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("贷款金额必须大于0");
            }            if (application.getAccountId() == null) {
                return Result.error("账户ID不能为空");
            }
            if (application.getTerm() == null || application.getTerm() <= 0) {
                return Result.error("贷款期限必须大于0");
            }
            
            application.setLoanApplicationRecordId(UUID.randomUUID().toString());
            application.setApplicationDate(LocalDate.now());
            application.setStatus("待审核");
            
            // 简单的信用评分逻辑
            if (application.getCreditScore() == null) {
                application.setCreditScore(650); // 默认信用评分
            }
            
            loanMapper.insertLoanApplication(application);
              Map<String, Object> data = new HashMap<>();
            data.put("applicationId", application.getLoanApplicationRecordId());
            data.put("status", application.getStatus());
            data.put("amount", application.getAmountOfMoney());
            
            return Result.success(data, 1);
        } catch (Exception e) {
            return Result.error("申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 审批贷款
     */
    @Transactional
    public Result<Map<String, Object>> approveLoan(String applicationId, String examinerId, String result) {
        try {
            LoanApplicationRecord application = loanMapper.findApplicationById(applicationId);
            if (application == null) {
                return Result.error("申请记录不存在");
            }
            
            if (!"待审核".equals(application.getStatus())) {
                return Result.error("该申请已经处理过了");
            }
            
            application.setLoanExaminerId(examinerId);
            application.setStatus(result);
            loanMapper.updateLoanApplication(application);
            
            Map<String, Object> data = new HashMap<>();
            data.put("applicationId", applicationId);
            data.put("status", result);
            
            if ("通过".equals(result)) {
                // 创建贷款记录
                Loan loan = new Loan();
                loan.setLoanId(UUID.randomUUID().toString());
                loan.setLoanApplicationRecordId(applicationId);
                loan.setAmountOfMoney(application.getAmountOfMoney());
                loan.setRepaymentDate(LocalDate.now().plusMonths(application.getTerm()));
                loan.setInterestRate(calculateInterestRate(application.getCreditScore()));
                loan.setIsOverdue(false);
                loan.setStatus("放款中");
                
                loanMapper.insertLoan(loan);
                  data.put("loanId", loan.getLoanId());
                data.put("repaymentDate", loan.getRepaymentDate());
                data.put("interestRate", loan.getInterestRate());
            }
            
            return Result.success(data, 1);
        } catch (Exception e) {
            return Result.error("审批失败: " + e.getMessage());
        }
    }
    
    /**
     * 贷款还款
     */
    @Transactional
    public Result<Map<String, Object>> repayLoan(String loanId, BigDecimal amount) {
        try {
            Loan loan = loanMapper.findLoanById(loanId);
            if (loan == null) {
                return Result.error("贷款记录不存在");
            }
            
            if ("已结清".equals(loan.getStatus())) {
                return Result.error("该贷款已经结清");
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("还款金额必须大于0");
            }
            
            // 计算应还总额（本金+利息）
            BigDecimal totalAmount = loan.getAmountOfMoney().multiply(
                BigDecimal.valueOf(1 + loan.getInterestRate())
            );
            
            if (amount.compareTo(totalAmount) >= 0) {
                loan.setStatus("已结清");
            } else {
                loan.setStatus("还款中");
            }
            
            loanMapper.updateLoan(loan);
              Map<String, Object> data = new HashMap<>();
            data.put("loanId", loanId);
            data.put("status", loan.getStatus());
            data.put("repayAmount", amount);
            data.put("totalAmount", totalAmount);
            
            return Result.success(data, 1);
        } catch (Exception e) {
            return Result.error("还款失败: " + e.getMessage());
        }
    }
      /**
     * 查询用户的贷款申请
     */
    public Result<List<LoanApplicationRecord>> getLoanApplications(Long accountId) {        try {
            List<LoanApplicationRecord> applications = loanMapper.findApplicationsByAccountId(accountId);
            return Result.success(applications, applications.size());
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询用户的贷款记录
     */
    public Result<List<Loan>> getLoans(Long accountId) {        try {
            List<Loan> loans = loanMapper.findLoansByAccountId(accountId);
            return Result.success(loans, loans.size());
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查逾期贷款
     */
    @Transactional
    public void checkOverdueLoans() {
        try {
            List<Loan> overdueLoans = loanMapper.findOverdueLoans();
            for (Loan loan : overdueLoans) {
                loan.setIsOverdue(true);
                loan.setStatus("已逾期");
                loanMapper.updateLoan(loan);
            }
        } catch (Exception e) {
            // 记录日志
            System.err.println("检查逾期贷款失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据信用评分计算利率
     */
    private Float calculateInterestRate(Integer creditScore) {
        if (creditScore >= 750) {
            return 0.05f; // 5%
        } else if (creditScore >= 700) {
            return 0.08f; // 8%
        } else if (creditScore >= 650) {
            return 0.12f; // 12%
        } else {
            return 0.18f; // 18%
        }
    }
}
