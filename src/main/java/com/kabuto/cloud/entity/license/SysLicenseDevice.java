package com.kabuto.cloud.entity.license;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 授权设备绑定实体
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的 Java 实体类</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_license_device 表</p>
 * <p><b>原因说明：</b>记录授权码与设备的绑定关系，支持多设备管理和设备验证</p>
 */
@Data
@TableName("sys_license_device")
public class SysLicenseDevice {

    /** 设备绑定ID */
    @TableId(value = "license_device_id", type = IdType.AUTO)
    private Long licenseDeviceId;

    /** 授权码ID */
    @TableField("license_id")
    private Long licenseId;

    /** 设备指纹 */
    @TableField("device_fingerprint")
    private String deviceFingerprint;

    /** 设备名称 */
    @TableField("device_name")
    private String deviceName;

    /** 最后验证时间 */
    @TableField("last_validated")
    private LocalDateTime lastValidated;

    /** 激活时间 */
    @TableField("activated_at")
    private LocalDateTime activatedAt;

    /** 是否激活状态 0=否 1=是 */
    @TableField("is_active")
    private Integer isActive;

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
