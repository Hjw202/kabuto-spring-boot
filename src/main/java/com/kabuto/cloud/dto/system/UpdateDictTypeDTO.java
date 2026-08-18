package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新字典类型请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UpdateDictTypeDto 实现编辑字典类型请求参数</p>
 * <p><b>解决方案：</b>继承 CreateDictTypeDTO 并添加 dictTypeId</p>
 * <p><b>原因说明：</b>对应 nest-admin UpdateDictTypeDto</p>
 */
@Data
@Schema(description = "更新字典类型请求")
public class UpdateDictTypeDTO {

    /** 字典类型ID */
    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "字典类型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

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
    private Integer status;
}
