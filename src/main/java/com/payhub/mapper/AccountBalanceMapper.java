package com.payhub.mapper;

import com.payhub.pojo.AccountBalance;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

@Mapper
public interface AccountBalanceMapper {

    @Select("select * from account_balance where account_id = #{accountId} and currency = #{currency}")
    AccountBalance selectByAccountIdAndCurrency(Long accountId, String currency);

    @Insert("insert into account_balance (account_id, balance, currency) values (#{accountId}, #{balance}, #{currency})")
    void insert(AccountBalance accountBalance);

    @Update("update account_balance set balance = balance + #{amount} where account_id = #{accountId} and currency = #{currency}")
    int increaseBalance(Long accountId, BigDecimal amount, String currency);

    @Update("update account_balance set balance = balance - #{amount} where account_id = #{accountId} and currency = #{currency} and balance >= #{amount}")
    int decreaseBalance(Long accountId, BigDecimal amount, String currency);
}