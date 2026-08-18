package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchDictDataDto 实现字典数据搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchDictDataDto</p>
 */
@Data
@Schema(description = "字典数据搜索条件")
public class SearchDictDataDTO {

    /** 字典标签 */
    @Schema(description = "字典标签")
    private String dictLabel;

    /** 字典类型 */
    @Schema(description = "字典类型")
    private String dictType;

    /** 状态 0=停用 1=启用 */
    @Schema(description = "状态")
    private Integer status;
}
