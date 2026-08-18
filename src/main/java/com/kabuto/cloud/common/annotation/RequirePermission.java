package com.kabuto.cloud.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 *
 * <p><b>需求描述：</b>大王要求实现类似 nest-admin @RequirePermission 的权限校验机制</p>
 * <p><b>解决方案：</b>自定义注解 + AOP 切面，从 Redis 缓存中读取当前用户权限列表并校验</p>
 * <p><b>原因说明：</b>对应 nest-admin 的 @RequirePermission 装饰器。
 * 支持单个权限字符串校验，超管（拥有 *:*:*）自动放行</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识，如 "system:user:add"
     */
    String value();
}
