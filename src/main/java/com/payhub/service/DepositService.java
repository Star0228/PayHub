package com.payhub.service;

import com.payhub.pojo.Deposit;
import com.payhub.pojo.DepositType;

import java.math.BigDecimal;
import java.util.List;

public interface DepositService {
    /**
     * 创建存款
     * @param userId 用户ID
     * @param type 存款类型
     * @param amount 存款金额
     * @return 存款记录
     */
    Deposit createDeposit(Long userId, DepositType type, BigDecimal amount);

    /**
     * 验证是否可以取款
     * @param depositId 存款ID
     * @return 是否可以取款
     */
    boolean canWithdraw(Long depositId);

    /**
     * 执行取款
     * @param depositId 存款ID
     * @return 取款金额
     */
    BigDecimal withdraw(Long depositId);

    /**
     * 根据ID获取存款记录
     * @param depositId 存款ID
     * @return 存款记录
     */
    Deposit getDepositById(Long depositId);

    /**
     * 计算存款利息
     * @param depositId 存款ID
     * @return 利息金额
     */
    BigDecimal calculateInterest(Long depositId);

    /**
     * 查询账户的所有存款记录
     * @param accountId 账户ID
     * @return 存款记录列表
     */
    List<Deposit> getDepositsByAccountId(Long accountId);
} 