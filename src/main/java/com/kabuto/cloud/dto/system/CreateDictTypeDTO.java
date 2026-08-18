package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建字典类型请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin CreateDictTypeDto 实现创建字典类型请求参数</p>
 * <p><b>解决方案：</b>字典类型 DTO，包含名称、类型键、状态</p>
 * <p><b>原因说明：</b>对应 nest-admin CreateDictTypeDto</p>
 */
@Data
@Schema(description = "创建字典类型请求")
public class CreateDictTypeDTO {

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    @Schema(description = "字典名称", example = "用户性别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型", example = "sys_user_sex", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /** 状态 0=停用 1=启用 */
    @Schema(description = "状态 0=停用 1=启用", example = "1")
    private Integer status = 1;
}
