package com.kabuto.cloud.common.enums;

import lombok.Getter;

/**
 * 状态枚举
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现 Java 版本认证系统</p>
 * <p><b>解决方案：</b>定义通用状态枚举，用于用户、角色、菜单等实体的状态标识</p>
 * <p><b>原因说明：</b>对应 nest-admin StatusEnum。使用字符 '0'/'1' 与数据库 char(1) 对齐</p>
 */
@Getter
public enum StatusEnum {

    /** 禁用 */
    DISABLE(0, "禁用"),

    /** 正常 */
    NORMAL(1, "正常");

    private final Integer code;
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
