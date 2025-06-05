package com.payhub.service.impl;

import com.payhub.mapper.DepositMapper;
import com.payhub.mapper.AccountMapper;
import com.payhub.mapper.AccountBalanceMapper;
import com.payhub.pojo.Deposit;
import com.payhub.pojo.DepositType;
import com.payhub.pojo.Account;
import com.payhub.pojo.AccountBalance;
import com.payhub.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DepositServiceImpl implements DepositService {

    private static final BigDecimal DEMAND_INTEREST_RATE = new BigDecimal("0.0035"); // 活期利率 0.35%
    private static final BigDecimal FIXED_INTEREST_RATE = new BigDecimal("0.0275"); // 定期利率 2.75%
    private static final int FIXED_TERM_MONTHS = 12; // 定期存款期限（月）

    @Autowired
    private DepositMapper depositMapper;
    
    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountBalanceMapper accountBalanceMapper;

    @Override
    public Deposit getDepositById(Long depositId) {
        return depositMapper.findById(depositId);
    }

    @Override
    @Transactional
    public Deposit createDeposit(Long accountId, DepositType type, BigDecimal amount) {
        // 先通过 accountId 查找账户
        Account account = accountMapper.selectByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("账户不存在");
        }

        // 检查并修改账户余额
        AccountBalance balance = accountBalanceMapper.selectByAccountIdAndCurrency(accountId, "CNY");
        if (balance == null) {
            // 如果余额记录不存在，创建一个新的余额记录
            balance = new AccountBalance();
            balance.setAccountId(accountId);
            balance.setCurrency("CNY");
            balance.setBalance(BigDecimal.ZERO);
            accountBalanceMapper.insert(balance);
        }

        // 增加账户余额
        int result = accountBalanceMapper.increaseBalance(accountId, amount, "CNY");
        if (result == 0) {
            throw new IllegalStateException("更新账户余额失败");
        }
        
        Deposit deposit = new Deposit();
        deposit.setAccountId(account.getAccountId());  // 使用 account 的 account_id
        deposit.setType(type);
        deposit.setAmount(amount);
        deposit.setCreateTime(LocalDateTime.now());
        deposit.setIsWithdrawn(false);
        deposit.setWithdrawTime(null);  // 初始时取款时间为null

        if (type == DepositType.FIXED) {
            deposit.setMaturityTime(LocalDateTime.now().plus(FIXED_TERM_MONTHS, ChronoUnit.MONTHS));
            deposit.setInterestRate(FIXED_INTEREST_RATE);
        } else {
            deposit.setInterestRate(DEMAND_INTEREST_RATE);
        }

        depositMapper.insert(deposit);
        return deposit;
    }

    @Override
    public boolean canWithdraw(Long depositId) {
        Deposit deposit = depositMapper.findById(depositId);
        if (deposit == null) {
            throw new IllegalArgumentException("存款记录不存在");
        }

        if (deposit.getIsWithdrawn()) {
            return false; // 已取款
        }

        if (deposit.getType() == DepositType.FIXED) {
            return LocalDateTime.now().isAfter(deposit.getMaturityTime()); // 定期存款必须到期才能取款
        }

        return true; // 活期存款随时可以取款
    }

    @Override
    @Transactional
    public BigDecimal withdraw(Long depositId) {
        if (!canWithdraw(depositId)) {
            throw new IllegalStateException("当前存款不能取款");
        }

        Deposit deposit = depositMapper.findById(depositId);
        if (deposit == null) {
            throw new IllegalArgumentException("存款记录不存在");
        }

        // 计算利息
        BigDecimal interest = calculateInterest(depositId);
        BigDecimal totalAmount = deposit.getAmount().add(interest);

        // 检查账户余额是否足够
        AccountBalance balance = accountBalanceMapper.selectByAccountIdAndCurrency(deposit.getAccountId(), "CNY");
        if (balance == null || balance.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("账户余额不足");
        }

        // 减少账户余额
        int result = accountBalanceMapper.decreaseBalance(deposit.getAccountId(), totalAmount, "CNY");
        if (result == 0) {
            throw new IllegalStateException("更新账户余额失败");
        }

        // 更新存款状态和取款时间
        LocalDateTime now = LocalDateTime.now();
        depositMapper.updateWithdrawStatus(depositId, true, now);

        return totalAmount;
    }

    @Override
    public BigDecimal calculateInterest(Long depositId) {
        Deposit deposit = depositMapper.findById(depositId);
        if (deposit == null) {
            throw new IllegalArgumentException("存款记录不存在");
        }
        return calculateInterest(deposit);
    }

    private BigDecimal calculateInterest(Deposit deposit) {
        LocalDateTime endTime;
        if (deposit.getIsWithdrawn()) {
            // 如果已取款，使用取款时间
            endTime = deposit.getWithdrawTime();
        } else {
            // 如果未取款，使用当前时间
            endTime = LocalDateTime.now();
        }
        
        long months = ChronoUnit.MONTHS.between(deposit.getCreateTime(), endTime);
        
        // 计算利息：本金 * 利率 * 月数
        return deposit.getAmount()
                .multiply(deposit.getInterestRate())
                .multiply(new BigDecimal(months));
    }

    @Override
    public List<Deposit> getDepositsByAccountId(Long accountId) {
        // 先检查账户是否存在
        Account account = accountMapper.selectByAccountId(accountId);
        if (account == null) {
            throw new IllegalArgumentException("账户不存在");
        }
        return depositMapper.findByAccountId(accountId);
    }
} 