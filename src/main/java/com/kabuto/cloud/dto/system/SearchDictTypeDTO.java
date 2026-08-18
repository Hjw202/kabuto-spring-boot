package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchDictDto 实现字典类型搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchDictDto</p>
 */
@Data
@Schema(description = "字典类型搜索条件")
public class SearchDictTypeDTO {

    /** 字典名称 */
    @Schema(description = "字典名称")
    private String dictName;

    /** 字典类型 */
    @Schema(description = "字典类型")
    private String dictType;

    /** 状态 0=停用 1=启用 */
    @Schema(description = "状态")
    private Integer status;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
