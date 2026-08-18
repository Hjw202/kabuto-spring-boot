package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchPermissionDto 实现权限搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchPermissionDto</p>
 */
@Data
@Schema(description = "权限搜索条件")
public class SearchPermissionDTO {

    /** 权限名称 */
    @Schema(description = "权限名称")
    private String name;

    /** 权限标识 */
    @Schema(description = "权限标识")
    private String perms;

    /** 状态 0=停用 1=正常 */
    @Schema(description = "状态 0=停用 1=正常")
    private Integer status;
}
