package com.ravey.almond.start.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravey.almond.api.utils.JwtUtils;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.core.user.UserInfo;
import com.ravey.common.service.web.result.HttpResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Token拦截器
 * 用于解析JWT Token并设置用户信息到上下文
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 放行Swagger等路径（如果需要更精细的控制，可以在WebConfig中配置excludePathPatterns）
        if (requestURI.contains("swagger") || requestURI.contains("api-docs")) {
            return true;
        }

        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            // 尝试从Header中获取UserInfo（兼容网关转发模式）
            String userInfoBase64 = request.getHeader("UserInfo");
            if (StringUtils.hasText(userInfoBase64)) {
                // 如果有UserInfo头，说明可能是网关转发过来的，交给TransmitCacheInfoInterceptor处理
                // 或者在这里也可以处理，但为了避免冲突，这里暂不处理，直接放行（假设TransmitCacheInfoInterceptor已配置）
                // 实际上，如果配置了TransmitCacheInfoInterceptor，它会先执行或后执行，取决于顺序
                // 建议：如果这里没拿到Token，但有UserInfo，则放行
                return true;
            }
            
            // 如果既没有Token也没有UserInfo，则报错
            log.warn("请求缺少token: {}", requestURI);
            writeErrorResponse(response, "缺少认证token", 401);
            return false;
        }

        // 验证token
        if (!jwtUtils.validateToken(token)) {
            log.warn("token无效或已过期: {}", requestURI);
            writeErrorResponse(response, "token无效或已过期", 401);
            return false;
        }

        // 解析用户信息
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            log.warn("无法从token中获取用户ID: {}", requestURI);
            writeErrorResponse(response, "token格式错误", 401);
            return false;
        }

        // 设置到UserCache
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(String.valueOf(userId));
        // 注意：JWT中可能不包含username，这里暂时设为userId或从其他地方获取
        // 如果需要完整的UserInfo，可能需要查库，但为了性能，建议只设置ID
        userInfo.setUsername(String.valueOf(userId)); 
        UserCache.setUserInfo(userInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserCache.clear();
    }

    private String extractToken(HttpServletRequest request) {
        // 优先从Header中获取 Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 从Header中获取 token
        String tokenHeader = request.getHeader("token");
        if (StringUtils.hasText(tokenHeader)) {
            return tokenHeader;
        }

        // 从参数中获取 token
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, String message, int status) {
        try {
            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            HttpResult<Object> result = HttpResult.failure(status, message);
            String jsonResponse = objectMapper.writeValueAsString(result);

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("写入错误响应失败", e);
        }
    }
}
