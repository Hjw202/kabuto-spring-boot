package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新字典数据请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UpdateDictDataDto 实现编辑字典数据请求参数</p>
 * <p><b>解决方案：</b>继承 CreateDictDataDTO 并添加 dictDataId</p>
 * <p><b>原因说明：</b>对应 nest-admin UpdateDictDataDto</p>
 */
@Data
@Schema(description = "更新字典数据请求")
public class UpdateDictDataDTO {

    /** 字典数据ID */
    @NotNull(message = "字典数据ID不能为空")
    @Schema(description = "字典数据ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /** 字典标签 */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 20, message = "字典标签长度不能超过20个字符")
    @Schema(description = "字典标签", example = "男", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictLabel;

    /** 字典键值 */
    @NotBlank(message = "字典键值不能为空")
    @Size(max = 20, message = "字典键值长度不能超过20个字符")
    @Schema(description = "字典键值", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictValue;

    /** 字典类型 */
    @Schema(description = "字典类型")
    private String dictType;

    /** 字典排序 */
    @Schema(description = "字典排序", example = "1")
    private Integer dictSort;

    /** 样式属性 */
    @Size(max = 100, message = "样式属性长度不能超过100个字符")
    @Schema(description = "样式属性")
    private String cssClass;

    /** 表格回显样式 */
    @Size(max = 100, message = "回显样式长度不能超过100个字符")
    @Schema(description = "表格回显样式")
    private String listClass;

    /** 是否默认 0=否 1=是 */
    @Schema(description = "是否默认 0=否 1=是", example = "0")
    private Integer isDefault;

    /** 状态 0=停用 1=启用 */
    @Schema(description = "状态 0=停用 1=启用", example = "1")
    private Integer status;
}
