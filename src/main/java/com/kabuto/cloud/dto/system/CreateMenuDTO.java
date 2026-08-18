package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建菜单请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin CreateMenuDto 实现创建菜单请求参数</p>
 * <p><b>解决方案：</b>菜单 DTO，包含路由信息 + 菜单类型 + 权限标识</p>
 * <p><b>原因说明：</b>对应 nest-admin CreateMenuDto。menuType=3(按钮)时自动关联权限标识</p>
 */
@Data
@Schema(description = "创建菜单请求")
public class CreateMenuDTO {

    /** 父菜单ID */
    @Schema(description = "父菜单ID（为空则为顶级菜单）")
    private Long parentId;

    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @Schema(description = "菜单名称", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 排序 */
    @NotNull(message = "排序不能为空")
    @Schema(description = "排序", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    /** 路由地址 */
    @Schema(description = "路由地址", example = "/system/user")
    private String router;

    /** 组件路径 */
    @Schema(description = "组件路径", example = "system/user/index")
    private String component;

    /** 路由参数 */
    @Schema(description = "路由参数")
    private String query;

    /** 是否外部链接 0=否 1=是 */
    @Schema(description = "是否外部链接 0=否 1=是", example = "0")
    private Integer isFrame = 0;

    /** 是否缓存 0=否 1=是 */
    @Schema(description = "是否缓存 0=否 1=是", example = "1")
    private Integer isCache = 1;

    /** 菜单类型 1=目录 2=页面 3=按钮 */
    @NotNull(message = "菜单类型不能为空")
    @Schema(description = "菜单类型 1=目录 2=页面 3=按钮", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer menuType;

    /** 显示状态 0=隐藏 1=显示 */
    @Schema(description = "显示状态 0=隐藏 1=显示", example = "1")
    private Integer visible = 1;

    /** 状态 0=停用 1=正常 */
    @Schema(description = "状态 0=停用 1=正常", example = "1")
    private Integer status = 1;

    /** 权限标识 */
    @Schema(description = "权限标识", example = "system:user:query")
    private String rule;

    /** 菜单图标 */
    @Schema(description = "菜单图标", example = "user")
    private String icon;
}
