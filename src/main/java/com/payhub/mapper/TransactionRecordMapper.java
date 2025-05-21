package com.payhub.mapper;

import com.payhub.pojo.TransactionRecord;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface TransactionRecordMapper {

    @Insert("insert into transaction_record (transaction_id, from_account_id, to_account_id, amount, currency, status, type, description) " +
            "values (#{transactionId}, #{fromAccountId}, #{toAccountId}, #{amount}, #{currency}, #{status}, #{type}, #{description})")
    void insert(TransactionRecord record);

    @Select("select * from transaction_record where transaction_id = #{transactionId}")
    TransactionRecord selectByTransactionId(String transactionId);

    @Update("update transaction_record set status = #{status}, completed_at = #{completedAt} where transaction_id = #{transactionId}")
    void updateTransactionStatus(String transactionId, String status, Date completedAt);
    
    @Select("select * from transaction_record where from_account_id = #{accountId} or to_account_id = #{accountId} order by created_at DESC")
    List<TransactionRecord> selectByAccountId(Long accountId);
}