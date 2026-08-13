package com.kabuto.cloud.common.enums;

import lombok.Getter;

/**
 * 登录状态枚举
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现 Java 版本认证系统</p>
 * <p><b>解决方案：</b>定义登录状态枚举，用于登录日志记录</p>
 * <p><b>原因说明：</b>对应 nest-admin LoginStatusEnum。'0'=失败，'1'=成功</p>
 */
@Getter
public enum LoginStatusEnum {

    /** 登录失败 */
    FAIL(0, "失败"),

    /** 登录成功 */
    SUCCESS(1, "成功");

    private final Integer code;
    private final String desc;

    LoginStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
