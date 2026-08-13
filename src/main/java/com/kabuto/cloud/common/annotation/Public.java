package com.kabuto.cloud.common.annotation;

import java.lang.annotation.*;

/**
 * 免认证注解
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 的 @Public 装饰器实现免认证功能</p>
 * <p><b>解决方案：</b>创建自定义注解，标记在 Controller 类或方法上，表示该接口无需 JWT 认证</p>
 * <p><b>原因说明：</b>对应 nest-admin 的 @Public() 装饰器。AuthTokenFilter 中检测到该注解直接放行</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Public {
}
