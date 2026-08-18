package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限实体
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin PermissionEntity 实现 Java 版本权限实体</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_permission 表</p>
 * <p><b>原因说明：</b>对应 nest-admin 的 PermissionEntity。perms 字段为权限标识字符串（如 system:user:add），
 * 用于前后端权限控制</p>
 */
@Data
@TableName("sys_permission")
public class SysPermission {

    /** 权限ID */
    @TableId(value = "permission_id", type = IdType.AUTO)
    private Long permissionId;

    /** 权限名称 */
    private String name;

    /** 权限标识 */
    private String perms;

    /** 描述 */
    private String description;

    /** 状态 0=停用 1=正常 */
    private Integer status;

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
