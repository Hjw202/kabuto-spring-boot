package com.kabuto.cloud.common.enums;

import lombok.Getter;

/**
 * 显示状态枚举
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现 Java 版本认证系统</p>
 * <p><b>解决方案：</b>定义显示状态枚举，用于菜单等实体的显示控制</p>
 * <p><b>原因说明：</b>对应 nest-admin VisibleEnum。'0'=隐藏，'1'=显示</p>
 */
@Getter
public enum VisibleEnum {

    /** 隐藏 */
    HIDDEN(0, "隐藏"),

    /** 显示 */
    VISIBLE(1, "显示");

    private final Integer code;
    private final String desc;

    VisibleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
