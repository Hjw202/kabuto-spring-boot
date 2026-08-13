package com.kabuto.cloud.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 的 @Throttle 装饰器实现限流功能</p>
 * <p><b>解决方案：</b>创建自定义注解，基于 Redis 滑动窗口计数器实现分布式限流</p>
 * <p><b>原因说明：</b>对应 nest-admin @Throttle 装饰器。轻量、分布式、可自定义限流维度和规则</p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Throttle {

    /**
     * 限流维度：key 的组成部分
     */
    enum Dimension {
        /** 按 IP 限流 */
        IP,
        /** 按 Token 限流 */
        TOKEN,
        /** 按用户ID限流 */
        USER_ID
    }

    /**
     * 限流维度（默认按 IP）
     */
    Dimension dimension() default Dimension.IP;

    /**
     * 时间窗口内允许的最大请求次数
     */
    int limit();

    /**
     * 时间窗口大小
     */
    long ttl();

    /**
     * 时间窗口单位（默认秒）
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 限流提示消息
     */
    String message() default "请求过于频繁，请稍后重试";
}
