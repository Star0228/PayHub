package com.payhub.interceptors;

import com.payhub.utils.JwtUtil;
import com.payhub.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 令牌验证
        String token = request.getHeader("Authorization");
        try {
            System.out.println("验证token: " + token);
            Map<String, Object> claims = JwtUtil.parseToken(token);
            // 检查必要的字段: accountId, userFlag
            if(claims.get("accountId") == null || claims.get("userFlag") == null) {
                System.out.println("Token缺少必要字段");
                response.setStatus(401);
                return false;
            }
            // 存入ThreadLocal
            UserContext.setAccountId(Long.valueOf(claims.get("accountId").toString()));
            UserContext.setUserFlag(Integer.valueOf(claims.get("userFlag").toString()));
            // 放行
            return true;
        } catch (Exception e) {
            System.out.println("无效的token: " + request.getRequestURI());
            response.setStatus(401);
            System.out.println("响应状态: " + response.getStatus());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
