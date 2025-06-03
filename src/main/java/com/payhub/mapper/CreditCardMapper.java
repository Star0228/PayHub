package com.payhub.mapper;

import com.payhub.pojo.CreditCard;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface CreditCardMapper {
    
    @Insert("INSERT INTO credit_card (account_id, card_number, is_lost, credit_limit, " +
            "available_credit, created_at, updated_at) " +
            "VALUES (#{accountId}, #{cardNumber}, #{isLost}, #{creditLimit}, " +
            "#{availableCredit}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CreditCard creditCard);
    
    @Select("SELECT * FROM credit_card WHERE id = #{id}")
    CreditCard findById(Long id);
    
    @Select("SELECT * FROM credit_card WHERE card_number = #{cardNumber}")
    CreditCard findByCardNumber(String cardNumber);
    
    @Select("SELECT * FROM credit_card WHERE account_id = #{accountId}")
    List<CreditCard> findByAccountId(Long accountId);
    
    @Update("UPDATE credit_card SET is_lost = #{isLost}, lost_time = #{lostTime}, " +
            "lost_reason = #{lostReason} WHERE id = #{id}")
    int updateLostStatus(@Param("id") Long id, @Param("isLost") Boolean isLost,
                        @Param("lostTime") LocalDateTime lostTime, @Param("lostReason") String lostReason);
                        
    @Update("UPDATE credit_card SET available_credit = #{availableCredit} WHERE id = #{id}")
    int updateAvailableCredit(@Param("id") Long id, @Param("availableCredit") BigDecimal availableCredit);
} 