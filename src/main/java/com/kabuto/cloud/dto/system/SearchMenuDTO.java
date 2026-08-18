package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchMenuDto 实现菜单搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchMenuDto</p>
 */
@Data
@Schema(description = "菜单搜索条件")
public class SearchMenuDTO {

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String name;

    /** 状态 0=停用 1=正常 */
    @Schema(description = "状态 0=停用 1=正常")
    private Integer status;
}
