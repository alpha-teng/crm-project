package com.crm.config;

import com.crm.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 排除 /api/auth/** 路径
        if (requestURI.startsWith("/api/auth/")) {
            return true;
        }
        
        // 只拦截 /api/** 路径
        if (!requestURI.startsWith("/api/")) {
            return true;
        }
        
        // 从Header中提取Authorization
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"未授权，请先登录\"}");
            return false;
        }
        
        String token = authHeader.substring(7);
        
        try {
            // 验证token
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Token无效或已过期\"}");
                return false;
            }
            
            // 解析token，获取userId
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 将userId存入request attribute
            request.setAttribute("userId", userId);
            
            log.debug("JWT验证通过, userId: {}", userId);
            return true;
            
        } catch (Exception e) {
            log.error("JWT拦截器处理失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Token验证失败\"}");
            return false;
        }
    }
}
