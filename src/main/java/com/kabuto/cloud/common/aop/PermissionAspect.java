package com.kabuto.cloud.common.aop;

import com.alibaba.fastjson2.JSON;
import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.annotation.RequireSuperAdmin;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.security.context.SecurityContext;
import com.kabuto.cloud.vo.system.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 权限校验 AOP 切面
 *
 * <p><b>需求描述：</b>大王要求实现类似 nest-admin @RequirePermission 的权限校验机制</p>
 * <p><b>解决方案：</b>AOP 拦截 @RequirePermission 注解，从 Redis 缓存读取当前用户权限列表并校验</p>
 * <p><b>原因说明：</b>对应 nest-admin 的权限守卫（AuthGuard）。
 * 超管（拥有 *:*:*）自动放行，与 AuthServiceImpl 中的缓存结构一致</p>
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    private final StringRedisTemplate redisTemplate;

    public PermissionAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(com.kabuto.cloud.common.annotation.RequirePermission)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

        String requiredPerm = requirePermission.value();
        if (!StringUtils.hasText(requiredPerm)) {
            return point.proceed();
        }

        // 获取当前用户ID
        String userId = SecurityContext.getUserId();
        if (!StringUtils.hasText(userId)) {
            throw new BizException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 从 Redis 获取用户信息（权限列表）
        String cacheKey = Constants.LOGIN_CACHE_TOKEN_KEY + userId;
        String cacheJson = redisTemplate.opsForValue().get(cacheKey);

        if (!StringUtils.hasText(cacheJson)) {
            throw new BizException(ResultCode.UNAUTHORIZED, "登录已过期，请重新登录");
        }

        try {
            UserInfoVO cached = JSON.parseObject(cacheJson, UserInfoVO.class);
            List<String> permissions = cached != null && cached.getPermissions() != null
                    ? cached.getPermissions()
                    : Collections.emptyList();

            // 超管放行（拥有 *:*:*）
            if (permissions.contains("*:*:*")) {
                return point.proceed();
            }

            // 校验权限
            if (!permissions.contains(requiredPerm)) {
                log.warn("[权限不足] userId={}, required={}, owned={}", userId, requiredPerm, permissions);
                throw new BizException(ResultCode.FORBIDDEN, "权限不足：" + requiredPerm);
            }
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("[权限校验异常] userId={}, error={}", userId, e.getMessage(), e);
            throw new BizException(ResultCode.UNAUTHORIZED, "登录信息异常，请重新登录");
        }

        return point.proceed();
    }

    /**
     * 超级管理员校验切面
     */
    @Around("@annotation(com.kabuto.cloud.common.annotation.RequireSuperAdmin)")
    public Object aroundSuperAdmin(ProceedingJoinPoint point) throws Throwable {
        String userId = SecurityContext.getUserId();
        if (!StringUtils.hasText(userId)) {
            throw new BizException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 超管ID为 1
        if (Constants.SUPER_ADMIN_ID.equals(userId)) {
            return point.proceed();
        }

        // 或拥有 *:*:* 权限
        String cacheKey = Constants.LOGIN_CACHE_TOKEN_KEY + userId;
        String cacheJson = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtils.hasText(cacheJson)) {
            try {
                UserInfoVO cached = JSON.parseObject(cacheJson, UserInfoVO.class);
                List<String> permissions = cached != null && cached.getPermissions() != null
                        ? cached.getPermissions()
                        : Collections.emptyList();

                if (permissions.contains("*:*:*")) {
                    return point.proceed();
                }
            } catch (Exception e) {
                log.error("[超管校验异常] userId={}, error={}", userId, e.getMessage(), e);
            }
        }

        log.warn("[非超管访问] userId={}", userId);
        throw new BizException(ResultCode.FORBIDDEN, "该操作仅允许超级管理员执行");
    }
}
