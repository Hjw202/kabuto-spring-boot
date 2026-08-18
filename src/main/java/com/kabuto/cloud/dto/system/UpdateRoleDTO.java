package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新角色请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UpdateRoleDto 实现编辑角色请求参数</p>
 * <p><b>解决方案：</b>继承 CreateRoleDTO 并添加 roleId</p>
 * <p><b>原因说明：</b>对应 nest-admin UpdateRoleDto</p>
 */
@Data
@Schema(description = "更新角色请求")
public class UpdateRoleDTO {

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    @Schema(description = "角色名称", example = "普通用户", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 权限字符 */
    @NotBlank(message = "权限字符不能为空")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    @Schema(description = "权限字符", example = "user", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleKey;

    /** 排序 */
    @NotNull(message = "排序不能为空")
    @Schema(description = "排序", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sort;

    /** 状态 0=禁用 1=正常 */
    @Schema(description = "状态 0=禁用 1=正常", example = "1")
    private Integer status;

    /** 角色描述 */
    @Size(max = 255, message = "描述长度不能超过255个字符")
    @Schema(description = "角色描述")
    private String description;

    /** 菜单ID列表 */
    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;

    /** 权限ID列表 */
    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
