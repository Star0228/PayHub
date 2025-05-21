package com.payhub.service.impl;

import com.payhub.mapper.UserMapper;
import com.payhub.pojo.User;
import com.payhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username)
    {
        return userMapper.selectByUsername(username);
    }

    @Override
    public String register(User user)
    {
        // 验证用户信息
        if (user.getUsername().length() < 6) {
            return "用户名必须至少6个字符";
        }
        if (!user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "邮箱格式不正确";
        }
        // 检查用户名和邮箱唯一性
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            return "用户名已存在";
        }
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            return "邮箱已存在";
        }

        userMapper.insert(user);
        return "注册成功！";
    }


    @Override
    public void resetPasswordByUsername(String username, String password)
    {
        userMapper.updatePasswordByUsername(username, password);
    }
}
