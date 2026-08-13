package com.kabuto.cloud.common.enums;

import lombok.Getter;

/**
 * 菜单类型枚举
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现 Java 版本认证系统</p>
 * <p><b>解决方案：</b>定义菜单类型枚举</p>
 * <p><b>原因说明：</b>对应 nest-admin MenuTypeEnum。'1'=目录，'2'=页面，'3'=接口</p>
 */
@Getter
public enum MenuTypeEnum {

    /** 目录 */
    DIRECTORY(1, "目录"),

    /** 页面 */
    PAGE(2, "页面"),

    /** 按钮 */
    BUTTON(3, "按钮");

    private final Integer code;
    private final String desc;

    MenuTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
