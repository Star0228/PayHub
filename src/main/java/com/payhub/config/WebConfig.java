package com.payhub.config;

import com.payhub.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录 / 注册的前端请求路径接口不能拦截
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/account/login",
                    "/account/register",
                    "/account/reset-password",
                    "/vcode/send",
                    // 存款管理相关API
                    "/deposit/create",
                    "/deposit/list",
                    "/deposit/can-withdraw",
                    "/deposit/withdraw",
                    // 转账管理相关API
                    "/transaction/transfer",
                    "/transaction/balance/**",
                    "/transaction/records/**",
                    "/transaction/records-range",
                    // 账户管理相关API
                    "/api/loan/apply",
                    "/api/loan/approve",
                    "/api/loan/repay",
                    "/api/loan/applications/**",
                    "/api/loan/loans/**",
                    "/api/loan/check-overdue",
                    "/api/forex/rate",
                    "/api/forex/rates",
                    "/api/forex/trade",
                    "/api/forex/trades/**",
                    "/api/forex/trade/**"
                    // 信用卡相关API
                    // "/credit-card/application/status/PENDING",
                    // "/credit-card/application/status"
                );
    }
}
