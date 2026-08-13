package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单实体
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin MenuEntity 实现 Java 版本菜单实体</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_menu 表</p>
 * <p><b>原因说明：</b>对应 nest-admin 的 MenuEntity。menuType 区分目录/页面/接口，
 * rule 字段存储权限标识（如 system:user:add），用于按钮级权限</p>
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    /** 菜单ID */
    @TableId(value = "menu_id", type = IdType.INPUT)
    private Long menuId;

    /** 父菜单ID */
    @TableField("parent_id")
    private Long parentId;

    /** 菜单名称 */
    private String name;

    /** 排序 */
    private Integer sort;

    /** 菜单图标 */
    private String icon;

    /** 路由地址 */
    private String router;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否外部链接 0=否 1=是 */
    @TableField("is_frame")
    private Integer isFrame;

    /** 是否缓存页面 0=否 1=是 */
    @TableField("is_cache")
    private Integer isCache;

    /** 菜单类型 1=目录 2=页面 3=按钮 */
    @TableField("menu_type")
    private Integer menuType;

    /** 状态 0=停用 1=启用 */
    private Integer status;

    /** 显示状态 0=隐藏 1=显示 */
    private Integer visible;

    /** 权限标识 */
    private String rule;

    /** 祖先路径，如 0,1,2 */
    private String ancestors;

    /** 关联权限ID */
    @TableField("permission_id")
    private Long permissionId;

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
}
