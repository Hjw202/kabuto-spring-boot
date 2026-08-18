package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictDataVO 实现字典数据详情响应</p>
 * <p><b>解决方案：</b>封装字典数据详细信息</p>
 * <p><b>原因说明：</b>对应 nest-admin DictDataVO</p>
 */
@Data
@Schema(description = "字典数据信息")
public class DictDataVO {

    @Schema(description = "字典数据ID")
    private Long dictDataId;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    private String dictValue;

    @Schema(description = "字典排序")
    private Integer dictSort;

    @Schema(description = "样式属性")
    private String cssClass;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "是否默认 0=否 1=是")
    private Integer isDefault;

    @Schema(description = "状态 0=停用 1=启用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
