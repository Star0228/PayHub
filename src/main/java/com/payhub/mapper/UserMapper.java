package com.payhub.mapper;

import com.payhub.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from users where users.username=#{username}")
    User selectByUsername(String username);

    @Insert("INSERT INTO users (username, password, email) " +
            "VALUES (#{username}, #{password}, #{email})")
    void insert(User user);

    @Select("select * from users where users.email=#{email}")
    User selectByEmail(String email);


    @Update("update users set password=#{password} where username=#{username}")
    void updatePasswordByUsername(String username, String password);
}
