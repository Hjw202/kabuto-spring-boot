package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求实现操作日志搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>操作日志审计查询需要按标题、业务类型、操作人、状态、时间范围筛选</p>
 */
@Data
@Schema(description = "操作日志搜索条件")
public class SearchOperLogDTO {

    /** 模块标题 */
    @Schema(description = "模块标题")
    private String title;

    /** 业务类型 0=其它 1=新增 2=修改 3=删除 */
    @Schema(description = "业务类型")
    private Integer businessType;

    /** 操作人员 */
    @Schema(description = "操作人员")
    private String operName;

    /** 操作状态 0=正常 1=失败 */
    @Schema(description = "操作状态 0=正常 1=失败")
    private Integer status;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
