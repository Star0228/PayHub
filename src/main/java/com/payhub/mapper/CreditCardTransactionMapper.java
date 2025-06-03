package com.payhub.mapper;

import com.payhub.pojo.CreditCardTransaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CreditCardTransactionMapper {
    
    @Insert("INSERT INTO credit_card_transaction (credit_card_id, account_id, type, amount, description, transaction_date) " +
            "VALUES (#{creditCardId}, #{accountId}, #{type}, #{amount}, #{description}, #{transactionTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CreditCardTransaction transaction);
    
    @Select("SELECT * FROM credit_card_transaction WHERE credit_card_id = #{creditCardId} " +
            "ORDER BY transaction_date DESC")
    List<CreditCardTransaction> findByCreditCardId(Long creditCardId);
    
    @Select("SELECT * FROM credit_card_transaction WHERE credit_card_id = #{creditCardId} " +
            "AND type = #{type} ORDER BY transaction_date DESC")
    List<CreditCardTransaction> findByCreditCardIdAndType(@Param("creditCardId") Long creditCardId,
                                                         @Param("type") String type);
} 