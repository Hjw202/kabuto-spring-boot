package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin RoleEntity 实现 Java 版本角色实体</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_role 表</p>
 * <p><b>原因说明：</b>对应 nest-admin 的 RoleEntity。roleKey 为权限字符标识，
 * 用于前端按钮级权限控制</p>
 */
@Data
@TableName("sys_role")
public class SysRole {

    /** 角色ID */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    /** 角色名称 */
    private String name;

    /** 角色权限字符 */
    @TableField("role_key")
    private String roleKey;

    /** 排序 */
    private Integer sort;

    /** 是否超级管理员 0=否 1=是 */
    @TableField("is_admin")
    private Integer isAdmin;

    /** 状态 0=禁用 1=正常 */
    private Integer status;

    /** 角色描述 */
    private String description;

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

    // ==================== 非数据库字段 ====================

    /** 菜单列表（关联查询用） */
    @TableField(exist = false)
    private List<SysMenu> menus;

    /** 权限列表（关联查询用） */
    @TableField(exist = false)
    private List<SysPermission> permissions;
}
