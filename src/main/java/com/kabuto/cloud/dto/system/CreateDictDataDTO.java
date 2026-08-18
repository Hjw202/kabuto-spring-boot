package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建字典数据请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin CreateDictDataDto 实现创建字典数据请求参数</p>
 * <p><b>解决方案：</b>字典数据 DTO，包含标签、值、类型、排序等</p>
 * <p><b>原因说明：</b>对应 nest-admin CreateDictDataDto</p>
 */
@Data
@Schema(description = "创建字典数据请求")
public class CreateDictDataDTO {

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
    @NotBlank(message = "字典类型不能为空")
    @Schema(description = "字典类型", example = "sys_user_sex", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /** 字典排序 */
    @NotNull(message = "字典排序不能为空")
    @Schema(description = "字典排序", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
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
    private Integer isDefault = 0;

    /** 状态 0=停用 1=启用 */
    @Schema(description = "状态 0=停用 1=启用", example = "1")
    private Integer status = 1;
}
