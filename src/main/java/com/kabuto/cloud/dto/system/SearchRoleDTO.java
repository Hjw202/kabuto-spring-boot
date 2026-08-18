package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchRoleDto 实现角色搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchRoleDto</p>
 */
@Data
@Schema(description = "角色搜索条件")
public class SearchRoleDTO {

    /** 角色名称 */
    @Schema(description = "角色名称")
    private String name;

    /** 权限字符 */
    @Schema(description = "权限字符")
    private String roleKey;

    /** 状态 0=禁用 1=正常 */
    @Schema(description = "状态 0=禁用 1=正常")
    private Integer status;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
