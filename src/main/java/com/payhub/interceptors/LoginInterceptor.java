package com.payhub.interceptors;

import com.payhub.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在每个前端请求到来前调用
        // 令牌验证
        String token = request.getHeader("Authorization");
        try {
            System.out.println(token);
            Map<String,Object> claims = JwtUtil.parseToken(token);
            // 返回 true 表示放行
            return true;
        } catch (Exception e) {
            System.out.println(request.getRequestURI());
            response.setStatus(401);
            System.out.println(response.getStatus());
            return false;
        }
    }
}
