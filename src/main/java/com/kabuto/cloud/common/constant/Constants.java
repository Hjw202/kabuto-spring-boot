package com.kabuto.cloud.common.constant;

/**
 * 系统常量
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 认证系统实现 Java 版本</p>
 * <p><b>解决方案：</b>定义系统核心常量，包括 Redis Key 前缀、超管ID、Token 相关常量</p>
 * <p><b>原因说明：</b>集中管理常量，避免硬编码，便于统一维护。对应 nest-admin Constants 类</p>
 */
public final class Constants {

    private Constants() {
        // 禁止实例化
    }

    // ==================== 用户相关 ====================

    /** 超级管理员用户ID */
    public static final String SUPER_ADMIN_ID = "1";

    // ==================== Token 相关 ====================

    /** Token 请求头名称 */
    public static final String TOKEN_HEADER = "Authorization";

    /** Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    // ==================== Redis Key 前缀 ====================

    /** 登录用户 Token 缓存前缀 */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /** 用户登录信息缓存前缀 */
    public static final String LOGIN_CACHE_TOKEN_KEY = "login_cache_tokens:";

    /** 系统配置缓存前缀 */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /** 限流计数缓存前缀 */
    public static final String THROTTLE_KEY = "throttle:";

    // ==================== 编码相关 ====================

    /** UTF-8 字符集 */
    public static final String UTF8 = "UTF-8";

}
