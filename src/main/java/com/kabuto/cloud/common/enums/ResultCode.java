package com.kabuto.cloud.common.enums;

import lombok.Getter;

/**
 * 业务响应状态码枚举
 *
 * <p><b>需求描述：</b>大王要求封装统一响应类，统一前后端交互数据结构</p>
 * <p><b>解决方案：</b>定义业务状态码枚举，与 HTTP 状态码解耦，支持自定义业务错误码</p>
 * <p><b>原因说明：</b>参考 nest-admin R.ts 的 HttpStatusEnum 设计，Java 项目通用做法。
 * 使用 ResultCode 而非 HttpStatusEnum 命名，因为业务码更灵活，可扩展非 HTTP 标准码（如 1001=参数错误）</p>
 */
@Getter
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "成功"),

    /** 请求参数错误 */
    BAD_REQUEST(400, "请求参数错误"),

    /** 未登录/未授权 */
    UNAUTHORIZED(401, "未授权"),

    /** 无权限访问 */
    FORBIDDEN(403, "您没有权限"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 请求方法不支持 */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /** 参数校验失败 */
    VALIDATION(422, "参数校验失败"),

    /** 请求过于频繁 */
    TOO_MANY_REQUESTS(429, "您的 IP 请求过于频繁"),

    /** 服务器内部错误 */
    ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
