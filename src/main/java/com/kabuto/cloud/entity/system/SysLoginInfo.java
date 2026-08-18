package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LoginInfoEntity 实现 Java 版本登录日志实体</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_login_info 表</p>
 * <p><b>原因说明：</b>对应 nest-admin 的 LoginInfoEntity。记录每次登录的账号、IP、地点、浏览器、结果等信息</p>
 */
@Data
@TableName("sys_login_info")
public class SysLoginInfo {

    /** 登录日志ID */
    @TableId(value = "login_info_id", type = IdType.AUTO)
    private Long loginInfoId;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 登录账号 */
    private String account;

    /** IP地址 */
    @TableField("ip_address")
    private String ipAddress;

    /** 登录地点 */
    @TableField("login_location")
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录状态 0=失败 1=成功 */
    private Integer status;

    /** 提示消息 */
    private String msg;

    /** 登录时间 */
    @TableField("login_time")
    private LocalDateTime loginTime;
}
