package com.payhub.service;

import com.payhub.mapper.LoanMapper;
import com.payhub.pojo.Loan;
import com.payhub.pojo.LoanApplicationRecord;
import com.payhub.pojo.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    private LoanApplicationRecord testApplication;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        testApplication = new LoanApplicationRecord();
        testApplication.setLoanApplicationRecordId("app123");
        testApplication.setAccountId(123L);
        testApplication.setAmountOfMoney(new BigDecimal("50000"));
        testApplication.setApplicationDate(LocalDate.now());
        testApplication.setCreditScore(700);
        testApplication.setStatus("待审核");
        testApplication.setTerm(12);
        testApplication.setPurpose("个人消费");

        testLoan = new Loan();
        testLoan.setLoanId("loan123");
        testLoan.setLoanApplicationRecordId("app123");
        testLoan.setAmountOfMoney(new BigDecimal("50000"));
        testLoan.setRepaymentDate(LocalDate.now().plusMonths(12));
        testLoan.setInterestRate(0.08f);
        testLoan.setIsOverdue(false);
        testLoan.setStatus("放款中");
    }

    @Test
    void testApplyLoan_Success() {
        // Given
        doNothing().when(loanMapper).insertLoanApplication(any(LoanApplicationRecord.class));

        // When
        Result<Map<String, Object>> result = loanService.applyLoan(testApplication);

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertNotNull(result.getData().get("applicationId"));
        assertEquals("待审核", result.getData().get("status"));
        assertEquals(new BigDecimal("50000"), result.getData().get("amount"));
        verify(loanMapper).insertLoanApplication(any(LoanApplicationRecord.class));
    }

    @Test
    void testApplyLoan_InvalidAmount() {
        // Given
        testApplication.setAmountOfMoney(BigDecimal.ZERO);

        // When
        Result<Map<String, Object>> result = loanService.applyLoan(testApplication);

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("贷款金额必须大于0"));
        verify(loanMapper, never()).insertLoanApplication(any());
    }

    @Test
    void testApplyLoan_EmptyAccountId() {
        // Given
        testApplication.setAccountId(null);

        // When
        Result<Map<String, Object>> result = loanService.applyLoan(testApplication);

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("账户ID不能为空"));
        verify(loanMapper, never()).insertLoanApplication(any());
    }

    @Test
    void testApplyLoan_InvalidTerm() {
        // Given
        testApplication.setTerm(0);

        // When
        Result<Map<String, Object>> result = loanService.applyLoan(testApplication);

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("贷款期限必须大于0"));
        verify(loanMapper, never()).insertLoanApplication(any());
    }

    @Test
    void testApplyLoan_DefaultCreditScore() {
        // Given
        testApplication.setCreditScore(null);
        doNothing().when(loanMapper).insertLoanApplication(any(LoanApplicationRecord.class));

        // When
        Result<Map<String, Object>> result = loanService.applyLoan(testApplication);

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        verify(loanMapper).insertLoanApplication(argThat(app -> app.getCreditScore() == 650));
    }

    @Test
    void testApproveLoan_ApprovalSuccess() {
        // Given
        when(loanMapper.findApplicationById("app123")).thenReturn(testApplication);
        doNothing().when(loanMapper).updateLoanApplication(any(LoanApplicationRecord.class));
        doNothing().when(loanMapper).insertLoan(any(Loan.class));

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("app123", "examiner1", "通过");

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("app123", result.getData().get("applicationId"));
        assertEquals("通过", result.getData().get("status"));
        assertNotNull(result.getData().get("loanId"));
        assertNotNull(result.getData().get("repaymentDate"));
        assertNotNull(result.getData().get("interestRate"));
        verify(loanMapper).updateLoanApplication(any(LoanApplicationRecord.class));
        verify(loanMapper).insertLoan(any(Loan.class));
    }

    @Test
    void testApproveLoan_RejectionSuccess() {
        // Given
        when(loanMapper.findApplicationById("app123")).thenReturn(testApplication);
        doNothing().when(loanMapper).updateLoanApplication(any(LoanApplicationRecord.class));

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("app123", "examiner1", "拒绝");

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("app123", result.getData().get("applicationId"));
        assertEquals("拒绝", result.getData().get("status"));
        assertNull(result.getData().get("loanId"));
        verify(loanMapper).updateLoanApplication(any(LoanApplicationRecord.class));
        verify(loanMapper, never()).insertLoan(any());
    }

    @Test
    void testApproveLoan_ApplicationNotFound() {
        // Given
        when(loanMapper.findApplicationById("nonexistent")).thenReturn(null);

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("nonexistent", "examiner1", "通过");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("申请记录不存在"));
        verify(loanMapper, never()).updateLoanApplication(any());
        verify(loanMapper, never()).insertLoan(any());
    }

    @Test
    void testApproveLoan_AlreadyProcessed() {
        // Given
        testApplication.setStatus("通过");
        when(loanMapper.findApplicationById("app123")).thenReturn(testApplication);

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("app123", "examiner1", "通过");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("该申请已经处理过了"));
        verify(loanMapper, never()).updateLoanApplication(any());
        verify(loanMapper, never()).insertLoan(any());
    }

    @Test
    void testRepayLoan_FullRepaymentSuccess() {
        // Given
        when(loanMapper.findLoanById("loan123")).thenReturn(testLoan);
        doNothing().when(loanMapper).updateLoan(any(Loan.class));

        BigDecimal fullAmount = testLoan.getAmountOfMoney().multiply(
            BigDecimal.valueOf(1 + testLoan.getInterestRate())
        );

        // When
        Result<Map<String, Object>> result = loanService.repayLoan("loan123", fullAmount);

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("loan123", result.getData().get("loanId"));
        assertEquals("已结清", result.getData().get("status"));
        assertEquals(fullAmount, result.getData().get("repayAmount"));
        verify(loanMapper).updateLoan(argThat(loan -> "已结清".equals(loan.getStatus())));
    }

    @Test
    void testRepayLoan_PartialRepaymentSuccess() {
        // Given
        when(loanMapper.findLoanById("loan123")).thenReturn(testLoan);
        doNothing().when(loanMapper).updateLoan(any(Loan.class));

        BigDecimal partialAmount = new BigDecimal("10000");

        // When
        Result<Map<String, Object>> result = loanService.repayLoan("loan123", partialAmount);

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("loan123", result.getData().get("loanId"));
        assertEquals("还款中", result.getData().get("status"));
        assertEquals(partialAmount, result.getData().get("repayAmount"));
        verify(loanMapper).updateLoan(argThat(loan -> "还款中".equals(loan.getStatus())));
    }

    @Test
    void testRepayLoan_LoanNotFound() {
        // Given
        when(loanMapper.findLoanById("nonexistent")).thenReturn(null);

        // When
        Result<Map<String, Object>> result = loanService.repayLoan("nonexistent", new BigDecimal("10000"));

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("贷款记录不存在"));
        verify(loanMapper, never()).updateLoan(any());
    }

    @Test
    void testRepayLoan_AlreadyPaidOff() {
        // Given
        testLoan.setStatus("已结清");
        when(loanMapper.findLoanById("loan123")).thenReturn(testLoan);

        // When
        Result<Map<String, Object>> result = loanService.repayLoan("loan123", new BigDecimal("10000"));

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("该贷款已经结清"));
        verify(loanMapper, never()).updateLoan(any());
    }

    @Test
    void testRepayLoan_InvalidAmount() {
        // Given
        when(loanMapper.findLoanById("loan123")).thenReturn(testLoan);

        // When
        Result<Map<String, Object>> result = loanService.repayLoan("loan123", BigDecimal.ZERO);

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("还款金额必须大于0"));
        verify(loanMapper, never()).updateLoan(any());
    }

    @Test
    void testGetLoanApplications_Success() {
        // Given
        List<LoanApplicationRecord> applications = Arrays.asList(testApplication);
        when(loanMapper.findApplicationsByAccountId(123L)).thenReturn(applications);

        // When
        Result<List<LoanApplicationRecord>> result = loanService.getLoanApplications(123L);

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("app123", result.getData().get(0).getLoanApplicationRecordId());
        verify(loanMapper).findApplicationsByAccountId(123L);
    }

    @Test
    void testGetLoans_Success() {
        // Given
        List<Loan> loans = Arrays.asList(testLoan);
        when(loanMapper.findLoansByAccountId(123L)).thenReturn(loans);

        // When
        Result<List<Loan>> result = loanService.getLoans(123L);

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("loan123", result.getData().get(0).getLoanId());
        verify(loanMapper).findLoansByAccountId(123L);
    }

    @Test
    void testCheckOverdueLoans_Success() {
        // Given
        List<Loan> overdueLoans = Arrays.asList(testLoan);
        when(loanMapper.findOverdueLoans()).thenReturn(overdueLoans);
        doNothing().when(loanMapper).updateLoan(any(Loan.class));

        // When
        loanService.checkOverdueLoans();

        // Then
        verify(loanMapper).findOverdueLoans();
        verify(loanMapper).updateLoan(argThat(loan -> 
            Boolean.TRUE.equals(loan.getIsOverdue()) && "已逾期".equals(loan.getStatus())
        ));
    }

    @Test
    void testCheckOverdueLoans_ExceptionHandling() {
        // Given
        when(loanMapper.findOverdueLoans()).thenThrow(new RuntimeException("Database error"));

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> loanService.checkOverdueLoans());
        verify(loanMapper).findOverdueLoans();
        verify(loanMapper, never()).updateLoan(any());
    }

    @Test
    void testApplyLoan_ExceptionHandling() {
        // Given
        doThrow(new RuntimeException("Database error")).when(loanMapper).insertLoanApplication(any());

        // When
        Result<Map<String, Object>> result = loanService.applyLoan(testApplication);

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("申请失败"));
    }

    @Test
    void testApproveLoan_ExceptionHandling() {
        // Given
        when(loanMapper.findApplicationById("app123")).thenReturn(testApplication);
        doThrow(new RuntimeException("Database error")).when(loanMapper).updateLoanApplication(any());

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("app123", "examiner1", "通过");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("审批失败"));
    }

    @Test
    void testRepayLoan_ExceptionHandling() {
        // Given
        when(loanMapper.findLoanById("loan123")).thenReturn(testLoan);
        doThrow(new RuntimeException("Database error")).when(loanMapper).updateLoan(any());

        // When
        Result<Map<String, Object>> result = loanService.repayLoan("loan123", new BigDecimal("10000"));

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("还款失败"));
    }

    @Test
    void testCalculateInterestRate_HighCreditScore() {
        // Test the private method through public method behavior
        // Given
        testApplication.setCreditScore(800);
        when(loanMapper.findApplicationById("app123")).thenReturn(testApplication);
        doNothing().when(loanMapper).updateLoanApplication(any());
        doNothing().when(loanMapper).insertLoan(any());

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("app123", "examiner1", "通过");

        // Then
        assertEquals(0, result.getCode());
        verify(loanMapper).insertLoan(argThat(loan -> loan.getInterestRate() == 0.05f));
    }

    @Test
    void testCalculateInterestRate_MediumCreditScore() {
        // Given
        testApplication.setCreditScore(720);
        when(loanMapper.findApplicationById("app123")).thenReturn(testApplication);
        doNothing().when(loanMapper).updateLoanApplication(any());
        doNothing().when(loanMapper).insertLoan(any());

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("app123", "examiner1", "通过");

        // Then
        assertEquals(0, result.getCode());
        verify(loanMapper).insertLoan(argThat(loan -> loan.getInterestRate() == 0.08f));
    }

    @Test
    void testCalculateInterestRate_LowCreditScore() {
        // Given
        testApplication.setCreditScore(600);
        when(loanMapper.findApplicationById("app123")).thenReturn(testApplication);
        doNothing().when(loanMapper).updateLoanApplication(any());
        doNothing().when(loanMapper).insertLoan(any());

        // When
        Result<Map<String, Object>> result = loanService.approveLoan("app123", "examiner1", "通过");

        // Then
        assertEquals(0, result.getCode());
        verify(loanMapper).insertLoan(argThat(loan -> loan.getInterestRate() == 0.18f));
    }

    @Test
    void testGetLoanApplications_ExceptionHandling() {
        // Given
        when(loanMapper.findApplicationsByAccountId(123L))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Result<List<LoanApplicationRecord>> result = loanService.getLoanApplications(123L);

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("查询失败"));
    }

    @Test
    void testGetLoans_ExceptionHandling() {
        // Given
        when(loanMapper.findLoansByAccountId(123L))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Result<List<Loan>> result = loanService.getLoans(123L);

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("查询失败"));
    }
}
