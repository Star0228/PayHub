package com.payhub.config;

import com.payhub.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录 / 注册的前端请求路径接口不能拦截
        registry.addInterceptor(loginInterceptor).excludePathPatterns(
                "/account/login",
                "/account/register",
                "/account/reset-password"
        );
    }
}
