package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin MenuVO 实现菜单详情响应</p>
 * <p><b>解决方案：</b>封装菜单详细信息，支持树形结构（children）</p>
 * <p><b>原因说明：</b>对应 nest-admin MenuVO。前端菜单管理页面需要树形展示</p>
 */
@Data
@Schema(description = "菜单信息")
public class MenuVO {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "路由地址")
    private String router;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "路由参数")
    private String query;

    @Schema(description = "是否外部链接 0=否 1=是")
    private Integer isFrame;

    @Schema(description = "是否缓存 0=否 1=是")
    private Integer isCache;

    @Schema(description = "菜单类型 1=目录 2=页面 3=按钮")
    private Integer menuType;

    @Schema(description = "状态 0=停用 1=正常")
    private Integer status;

    @Schema(description = "显示状态 0=隐藏 1=显示")
    private Integer visible;

    @Schema(description = "权限标识")
    private String rule;

    @Schema(description = "关联权限ID")
    private Long permissionId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "子菜单列表")
    private List<MenuVO> children;
}
