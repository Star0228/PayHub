package com.payhub.service;

import com.payhub.pojo.User;

public interface UserService {

    public String register(User user);

    public User findByUsername(String username);

    void resetPasswordByUsername(String username, String password);
}
