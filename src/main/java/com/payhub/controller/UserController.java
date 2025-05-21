package com.payhub.controller;

import com.payhub.pojo.Result;
import com.payhub.pojo.User;
import com.payhub.service.UserService;
import com.payhub.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // 返回的Result对象是通过调用 Result 类的静态方法而创建的。

    @PostMapping(value = "/register", produces = "application/json")
    public Result register(@RequestBody User user) {
        System.out.println("Received registering user: " + user); // 调试
        String registerFeedBack = userService.register(user);
        //return Result.success(user);
        if(Objects.equals(registerFeedBack, "注册成功！")){
            return Result.success();
        }else {
            System.out.println(registerFeedBack);
            return Result.error(registerFeedBack);
        }
    }

    @PostMapping(value = "/login", produces = "application/json")
    public Result login(@RequestBody User user) {
        System.out.println("Received logging user: " + user);
        User loginUser = userService.findByUsername(user.getUsername());

        if(loginUser == null) {
            System.out.println("Login failed");
            return Result.error("用户名错误");
        }
        System.out.println("Username exist");

        if(loginUser.getPassword().equals(user.getPassword())) {
            System.out.println("Login/reset-password success");

            //jwt
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",loginUser.getId());
            claims.put("username",loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            // 响应数据中的 data 即对应令牌
            System.out.println("Returning result: " + Result.success(token, 3));
            return Result.success(token, 3);
        }else{
            return Result.error("密码错误");
        }
    }

    @PostMapping(value = "/reset-password", produces = "application/json")
    public Result resetPassword(@RequestBody User user) {
        System.out.println("Received reset password user: " + user);

        userService.resetPasswordByUsername(user.getUsername(), user.getPassword());
        return Result.success();
    }
}
