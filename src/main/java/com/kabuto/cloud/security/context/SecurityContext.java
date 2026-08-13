package com.kabuto.cloud.security.context;

/**
 * 安全上下文（线程本地存储）
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 中 req.userId 的实现方式</p>
 * <p><b>解决方案：</b>使用 ThreadLocal 存储当前请求的用户ID，供 Controller/Service 层随时获取</p>
 * <p><b>原因说明：</b>ThreadLocal 实现请求线程隔离，避免方法传参污染。对应 nest-admin 中 @Req() req: Request 的 userId 属性</p>
 */
public class SecurityContext {

    private static final ThreadLocal<String> USER_ID_HOLDER = new ThreadLocal<>();

    private SecurityContext() {
        // 禁止实例化
    }

    /**
     * 设置当前用户ID
     */
    public static void setUserId(String userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static String getUserId() {
        return USER_ID_HOLDER.get();
    }

    /**
     * 清除当前用户ID（防止线程池复用导致数据泄漏）
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
    }
}
