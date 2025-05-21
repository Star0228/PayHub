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
        // String token = request.getHeader("Authorization");
        // try {
        //     System.out.println("验证token: " + token);
        //     Map<String, Object> claims = JwtUtil.parseToken(token);
        //     // 检查必要的字段: id, accountId, username
        //     if(claims.get("id") == null || claims.get("accountId") == null || claims.get("username") == null) {
        //         System.out.println("Token缺少必要字段");
        //         response.setStatus(401);
        //         return false;
        //     }
        //     // 返回 true 表示放行
        //     return true;
        // } catch (Exception e) {
        //     System.out.println("无效的token: " + request.getRequestURI());
        //     response.setStatus(401);
        //     System.out.println("响应状态: " + response.getStatus());
        //     return false;
        // }

        // 暂时禁用 jwt，需要和前端协商令牌保存方式
        return true;
    }
}
