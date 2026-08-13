package com.kabuto.cloud.common.enums;

import lombok.Getter;

/**
 * 性别枚举
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现 Java 版本认证系统</p>
 * <p><b>解决方案：</b>定义性别枚举</p>
 * <p><b>原因说明：</b>对应 nest-admin SexEnum。字符 '0'='女'，'1'='男'</p>
 */
@Getter
public enum SexEnum {

    /** 女 */
    WOMAN(0, "女"),

    /** 男 */
    MAN(1, "男");

    private final Integer code;
    private final String desc;

    SexEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
