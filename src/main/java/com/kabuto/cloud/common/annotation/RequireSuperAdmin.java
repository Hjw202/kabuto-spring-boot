package com.kabuto.cloud.common.annotation;

import java.lang.annotation.*;

/**
 * 超级管理员校验注解
 *
 * <p><b>需求描述：</b>大王要求新增超管校验注解，对应 nest-admin 的 @Root 装饰器</p>
 * <p><b>解决方案：</b>自定义注解 + AOP 切面，校验当前用户是否为超级管理员</p>
 * <p><b>原因说明：</b>部分危险操作（如删除角色、重置密码、启停用户状态）仅允许超管执行。
 * 超管判定：userId = 1 或拥有 *:*:* 权限</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireSuperAdmin {
}
