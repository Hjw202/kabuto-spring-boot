package com.kabuto.cloud.security.filter;

import com.kabuto.cloud.common.annotation.Public;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.security.context.SecurityContext;
import com.kabuto.cloud.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 认证 Token 过滤器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 的认证机制实现 JWT Token 校验</p>
 * <p><b>解决方案：</b>基于 OncePerRequestFilter 实现认证过滤器，在请求到达 Controller 前完成 Token 校验</p>
 * <p><b>原因说明：</b>OncePerRequestFilter 比 HandlerInterceptor 执行更早、更标准，能拦截所有请求类型。
 * 检测到 @Public 注解直接放行；否则校验 JWT 有效性并解析用户ID存入 ThreadLocal。
 * 对应 nest-admin 中 JWT Guard 的功能</p>
 */
@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final RequestMappingHandlerMapping handlerMapping;

    public AuthTokenFilter(JwtUtil jwtUtil, StringRedisTemplate redisTemplate, RequestMappingHandlerMapping handlerMapping) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 检查是否为公开接口（免认证）
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. 提取 Token
            String token = extractToken(request);
            if (token == null) {
                writeErrorResponse(response, ResultCode.UNAUTHORIZED, "未提供认证 Token");
                return;
            }

            // 3. 验证 JWT 签名和过期时间
            if (!jwtUtil.validateToken(token)) {
                writeErrorResponse(response, ResultCode.UNAUTHORIZED, "Token 无效或已过期");
                return;
            }

            // 4. 解析 tokenId，校验 Redis 中是否存在（防止 Token 被登出后仍可使用）
            String tokenId = jwtUtil.getTokenId(token);
            if (tokenId == null) {
                writeErrorResponse(response, ResultCode.UNAUTHORIZED, "Token 格式错误");
                return;
            }

            String redisKey = Constants.LOGIN_TOKEN_KEY + tokenId;
            Boolean hasKey = redisTemplate.hasKey(redisKey);
            if (Boolean.FALSE.equals(hasKey)) {
                writeErrorResponse(response, ResultCode.UNAUTHORIZED, "Token 已失效，请重新登录");
                return;
            }

            // 5. 提取用户ID并存入 SecurityContext
            String userId = jwtUtil.getUserId(token);
            if (userId != null) {
                SecurityContext.setUserId(userId);
            }

            // 6. 放行
            filterChain.doFilter(request, response);

        } catch (BizException e) {
            writeErrorResponse(response, e.getResultCode(), e.getMessage());
        } catch (Exception e) {
            log.error("[认证过滤器] 异常: {}", e.getMessage(), e);
            writeErrorResponse(response, ResultCode.UNAUTHORIZED, "认证失败");
        } finally {
            // 清理 ThreadLocal，防止线程池复用导致数据泄漏
            SecurityContext.clear();
        }
    }

    /**
     * 判断当前请求是否为公开接口（带有 @Public 注解）
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        try {
            HandlerExecutionChain chain = handlerMapping.getHandler(request);
            if (chain == null) {
                return false;
            }

            Object handler = chain.getHandler();
            if (!(handler instanceof HandlerMethod handlerMethod)) {
                return false;
            }

            // 检查方法上是否有 @Public 注解
            if (handlerMethod.hasMethodAnnotation(Public.class)) {
                return true;
            }

            // 检查类上是否有 @Public 注解
            return handlerMethod.getBeanType().isAnnotationPresent(Public.class);

        } catch (Exception e) {
            log.warn("[认证过滤器] 判断公开接口异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从请求头中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
            return bearerToken.substring(Constants.TOKEN_PREFIX.length()).trim();
        }
        return null;
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, ResultCode code, String message) throws IOException {
        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String json = String.format("{\"code\":%d,\"data\":null,\"msg\":\"%s\"}", code.getCode(), message);
        response.getWriter().write(json);
    }
}
