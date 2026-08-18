package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UserEntity 实现 Java 版本用户实体</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_user 表，包含用户基本信息和角色/权限关联</p>
 * <p><b>原因说明：</b>对应 nest-admin 的 UserEntity。使用 @TableLogic 实现逻辑删除，
 * @TableField 实现字段映射。roles 和 permissions 为非数据库字段，用于关联查询结果承载</p>
 */
@Data
@TableName("sys_user")
public class SysUser {

    /** 用户ID */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /** 账号 */
    private String username;

    /** 密码 */
    private String password;

    /** 昵称 */
    private String name;

    /** 生日 */
    private LocalDate birthday;

    /** 年龄 */
    @TableField(exist = false)
    private Integer age;

    /** 性别 0=女 1=男 */
    private Integer sex;

    /** 手机号码 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 头像 */
    private String avatar;

    /** 状态 0=禁用 1=正常 */
    private Integer status;

    /** 上次登录IP */
    @TableField("login_ip")
    private String loginIp;

    /** 上次登录时间 */
    @TableField("login_date")
    private LocalDateTime loginDate;

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

    /** 角色列表（关联查询用） */
    @TableField(exist = false)
    private List<SysRole> roles;

    /** 权限列表（关联查询用） */
    @TableField(exist = false)
    private List<SysPermission> permissions;
}
