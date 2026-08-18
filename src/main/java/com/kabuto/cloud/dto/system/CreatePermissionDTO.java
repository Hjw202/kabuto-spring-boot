package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建权限请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin CreatePermissionDto 实现创建权限请求参数</p>
 * <p><b>解决方案：</b>权限 DTO，包含权限名称和标识</p>
 * <p><b>原因说明：</b>对应 nest-admin CreatePermissionDto</p>
 */
@Data
@Schema(description = "创建权限请求")
public class CreatePermissionDTO {

    /** 权限名称 */
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100个字符")
    @Schema(description = "权限名称", example = "用户查询", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 权限标识 */
    @NotBlank(message = "权限标识不能为空")
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @Schema(description = "权限标识", example = "system:user:query", requiredMode = Schema.RequiredMode.REQUIRED)
    private String perms;

    /** 描述 */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Schema(description = "描述")
    private String description;

    /** 状态 0=停用 1=正常 */
    @Schema(description = "状态 0=停用 1=正常", example = "1")
    private Integer status = 1;
}
