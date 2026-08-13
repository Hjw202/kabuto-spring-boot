package com.kabuto.cloud.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 路由 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin getRouters 接口响应实现路由 VO</p>
 * <p><b>解决方案：</b>封装菜单路由信息，用于前端动态路由生成</p>
 * <p><b>原因说明：</b>对应 nest-admin getRouters 返回的 MenuEntity 子集。
 * 过滤掉 createTime/updateTime 等系统字段，只保留前端需要的字段</p>
 */
@Data
@Schema(description = "路由信息")
public class RouterVO {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

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

    @Schema(description = "菜单类型 1=目录 2=页面")
    private Integer menuType;

    @Schema(description = "显示状态 0=隐藏 1=显示")
    private Integer visible;

    @Schema(description = "权限标识")
    private String rule;

    @Schema(description = "菜单图标")
    private String icon;
}
