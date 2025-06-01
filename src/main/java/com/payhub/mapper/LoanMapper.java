package com.payhub.mapper;

import com.payhub.pojo.LoanApplicationRecord;
import com.payhub.pojo.Loan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LoanMapper {
    
    @Insert("INSERT INTO LoanApplicationRecord (loan_application_record_id, account_id, amount_of_money, application_date, credit_score, status, term, purpose) " +
            "VALUES (#{loanApplicationRecordId}, #{accountId}, #{amountOfMoney}, #{applicationDate}, #{creditScore}, #{status}, #{term}, #{purpose})")
    void insertLoanApplication(LoanApplicationRecord application);
    
    @Select("SELECT * FROM LoanApplicationRecord WHERE loan_application_record_id = #{id}")
    LoanApplicationRecord findApplicationById(String id);
      @Select("SELECT * FROM LoanApplicationRecord WHERE account_id = #{accountId}")
    List<LoanApplicationRecord> findApplicationsByAccountId(Long accountId);
    
    @Select("SELECT * FROM LoanApplicationRecord WHERE status = #{status}")
    List<LoanApplicationRecord> findApplicationsByStatus(String status);
    
    @Update("UPDATE LoanApplicationRecord SET loan_examiner_id = #{loanExaminerId}, status = #{status} WHERE loan_application_record_id = #{loanApplicationRecordId}")
    void updateLoanApplication(LoanApplicationRecord application);
    
    @Insert("INSERT INTO Loan (loan_id, loan_application_record_id, amount_of_money, repayment_date, interest_rate, is_overdue, status) " +
            "VALUES (#{loanId}, #{loanApplicationRecordId}, #{amountOfMoney}, #{repaymentDate}, #{interestRate}, #{isOverdue}, #{status})")
    void insertLoan(Loan loan);
    
    @Select("SELECT * FROM Loan WHERE loan_id = #{id}")
    Loan findLoanById(String id);
    
    @Select("SELECT * FROM Loan WHERE loan_application_record_id = #{applicationId}")
    Loan findLoanByApplicationId(String applicationId);
      @Select("SELECT l.* FROM Loan l INNER JOIN LoanApplicationRecord lar ON l.loan_application_record_id = lar.loan_application_record_id WHERE lar.account_id = #{accountId}")
    List<Loan> findLoansByAccountId(Long accountId);
    
    @Update("UPDATE Loan SET status = #{status}, is_overdue = #{isOverdue} WHERE loan_id = #{loanId}")
    void updateLoan(Loan loan);
    
    @Select("SELECT * FROM Loan WHERE repayment_date < CURDATE() AND status IN ('放款中', '还款中') AND is_overdue = false")
    List<Loan> findOverdueLoans();
}
