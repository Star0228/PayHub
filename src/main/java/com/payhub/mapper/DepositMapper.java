package com.payhub.mapper;

import com.payhub.pojo.Deposit;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DepositMapper {
    
    @Insert("INSERT INTO deposit (account_id, type, amount, create_time, maturity_time, interest_rate, is_withdrawn, withdraw_time) " +
            "VALUES (#{accountId}, #{type}, #{amount}, #{createTime}, #{maturityTime}, #{interestRate}, #{isWithdrawn}, #{withdrawTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Deposit deposit);

    @Select("SELECT * FROM deposit WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "accountId", column = "account_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "amount", column = "amount"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "maturityTime", column = "maturity_time"),
        @Result(property = "interestRate", column = "interest_rate"),
        @Result(property = "isWithdrawn", column = "is_withdrawn"),
        @Result(property = "withdrawTime", column = "withdraw_time")
    })
    Deposit findById(Long id);

    @Select("SELECT * FROM deposit WHERE account_id = #{accountId} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "accountId", column = "account_id"),
        @Result(property = "type", column = "type"),
        @Result(property = "amount", column = "amount"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "maturityTime", column = "maturity_time"),
        @Result(property = "interestRate", column = "interest_rate"),
        @Result(property = "isWithdrawn", column = "is_withdrawn"),
        @Result(property = "withdrawTime", column = "withdraw_time")
    })
    List<Deposit> findByAccountId(Long accountId);

    @Update("UPDATE deposit SET is_withdrawn = #{isWithdrawn}, withdraw_time = #{withdrawTime} WHERE id = #{id}")
    int updateWithdrawStatus(@Param("id") Long id, @Param("isWithdrawn") Boolean isWithdrawn, @Param("withdrawTime") LocalDateTime withdrawTime);
} 