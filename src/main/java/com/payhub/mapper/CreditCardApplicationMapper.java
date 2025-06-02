package com.payhub.mapper;

import com.payhub.pojo.CreditCardApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface CreditCardApplicationMapper {
    
    @Insert("INSERT INTO credit_card_application (account_id, status, application_date) " +
            "VALUES (#{accountId}, #{status}, #{applicationDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CreditCardApplication application);

    @Select("SELECT * FROM credit_card_application WHERE id = #{id}")
    CreditCardApplication findById(Long id);

    @Select("SELECT * FROM credit_card_application WHERE account_id = #{accountId}")
    List<CreditCardApplication> findByAccountId(Long accountId);

    @Select("SELECT * FROM credit_card_application WHERE status = #{status}")
    List<CreditCardApplication> findByStatus(String status);

    @Update("UPDATE credit_card_application SET status = #{status}, reviewer_id = #{reviewerId}, " +
            "review_date = #{reviewDate} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, 
                    @Param("reviewerId") Long reviewerId, @Param("reviewDate") LocalDateTime reviewDate);
} 