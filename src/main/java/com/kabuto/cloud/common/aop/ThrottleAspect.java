package com.kabuto.cloud.common.aop;

import com.kabuto.cloud.common.annotation.Throttle;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.security.context.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 限流 AOP 切面
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 的 @Throttle 实现限流功能</p>
 * <p><b>解决方案：</b>基于 Redis 滑动窗口计数器实现分布式限流。使用 AOP 拦截带 @Throttle 注解的方法</p>
 * <p><b>原因说明：</b>Redis INCR + EXPIRE 实现轻量级滑动窗口，支持 IP/Token/用户ID 多维度限流。
 * 对应 nest-admin @nestjs/throttler 的功能</p>
 */
@Slf4j
@Aspect
@Component
public class ThrottleAspect {

    private final StringRedisTemplate redisTemplate;

    public ThrottleAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(com.kabuto.cloud.common.annotation.Throttle)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Throttle throttle = method.getAnnotation(Throttle.class);

        String key = buildKey(throttle);
        if (key == null) {
            // 无法获取限流维度，直接放行
            return point.proceed();
        }

        String redisKey = Constants.THROTTLE_KEY + key;
        long ttl = throttle.unit().toSeconds(throttle.ttl());
        int limit = throttle.limit();

        // 使用 Redis 计数器实现滑动窗口限流
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1) {
            // 第一次请求，设置过期时间
            redisTemplate.expire(redisKey, ttl, TimeUnit.SECONDS);
        }

        if (count != null && count > limit) {
            log.warn("[限流拦截] key={}, count={}, limit={}, method={}", key, count, limit, method.getName());
            throw new BizException(R.frequent(throttle.message()).getMsg());
        }

        return point.proceed();
    }

    /**
     * 构建限流 key
     */
    private String buildKey(Throttle throttle) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        StringBuilder key = new StringBuilder();

        // 添加接口路径作为 key 的一部分
        key.append(request.getRequestURI()).append(":");

        switch (throttle.dimension()) {
            case IP:
                key.append(getClientIp(request));
                break;
            case TOKEN:
                String token = request.getHeader(Constants.TOKEN_HEADER);
                if (token != null && token.startsWith(Constants.TOKEN_PREFIX)) {
                    key.append(token.substring(Constants.TOKEN_PREFIX.length()));
                } else {
                    key.append("anonymous");
                }
                break;
            case USER_ID:
                String userId = SecurityContext.getUserId();
                if (userId != null) {
                    key.append(userId);
                } else {
                    key.append("anonymous");
                }
                break;
            default:
                key.append(getClientIp(request));
        }

        return key.toString();
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
