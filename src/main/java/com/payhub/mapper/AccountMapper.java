package com.payhub.mapper;

import com.payhub.pojo.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountMapper {

    @Select("select * from account where username = #{username}")
    Account selectByUsername(String username);

    @Select("select * from account where email = #{email}")
    Account selectByEmail(String email);

    @Select("select * from account where account_id = #{accountId}")
    Account selectByAccountId(Long accountId);

    @Select("select max(account_id) from account")
    Long getMaxAccountId();

    @Insert("insert into account (id, account_id, password, card_id, username, email, address, gender, occupation, phone_number, annual_income, user_flag) " +
            "values (#{id}, #{accountId}, #{password}, #{cardId}, #{username}, #{email}, #{address}, #{gender}, #{occupation}, #{phoneNumber}, #{annualIncome}, #{userFlag})")
    void insert(Account account);

    @Update("update account set password = #{password} where username = #{username}")
    void updatePasswordByUsername(String username, String password);
}