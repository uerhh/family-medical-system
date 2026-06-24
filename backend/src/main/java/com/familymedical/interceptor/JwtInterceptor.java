package com.familymedical.interceptor;

import com.familymedical.common.Constants;
import com.familymedical.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(Constants.TOKEN_HEADER);
        if (token == null || !token.startsWith(Constants.TOKEN_PREFIX)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或token无效\"}");
            return false;
        }

        token = token.substring(Constants.TOKEN_PREFIX.length());
        try {
            Long userId = jwtUtil.getUserId(token);
            Integer role = jwtUtil.getRole(token);
            request.setAttribute(Constants.USER_ID_KEY, userId);
            request.setAttribute(Constants.ROLE_KEY, role);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token已过期或无效\"}");
            return false;
        }
    }
}
