package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictTypeVO 实现字典类型详情响应</p>
 * <p><b>解决方案：</b>封装字典类型详细信息</p>
 * <p><b>原因说明：</b>对应 nest-admin DictTypeVO</p>
 */
@Data
@Schema(description = "字典类型信息")
public class DictTypeVO {

    @Schema(description = "字典类型ID")
    private Long dictTypeId;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "状态 0=停用 1=启用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
