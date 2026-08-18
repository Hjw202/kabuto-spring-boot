package com.kabuto.cloud.entity.license;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 授权码实体
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的 Java 实体类</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_license 表</p>
 * <p><b>原因说明：</b>授权码表管理软件授权许可，支持多种计划类型和过期控制</p>
 */
@Data
@TableName("sys_license")
public class SysLicense {

    /** 授权码ID */
    @TableId(value = "license_id", type = IdType.AUTO)
    private Long licenseId;

    /** 授权码 */
    @TableField("license_code")
    private String licenseCode;

    /** 计划类型 trial/basic/pro/enterprise/permanent */
    @TableField("plan_type")
    private String planType;

    /** 最大设备数 */
    @TableField("max_devices")
    private Integer maxDevices;

    /** 授权状态 0=未使用 1=已激活 2=已过期 3=已禁用 */
    private Integer status;

    /** 激活时间 */
    @TableField("activated_at")
    private LocalDateTime activatedAt;

    /** 过期时间，null表示永久 */
    @TableField("expires_at")
    private LocalDateTime expiresAt;

    /** ECDSA签名 */
    private String signature;

    /** 备注 */
    private String note;

    /** 创建者 */
    @TableField("create_by")
    private String createBy;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField("update_by")
    private String updateBy;

    /** 更新时间 */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /** 逻辑删除 0=正常 1=删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
