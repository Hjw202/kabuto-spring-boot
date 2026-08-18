package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志信息 VO
 *
 * <p><b>需求描述：</b>大王要求实现操作日志详情响应</p>
 * <p><b>解决方案：</b>封装操作日志详细信息</p>
 * <p><b>原因说明：</b>操作日志用于审计和问题追踪，需要展示完整的请求和响应信息</p>
 */
@Data
@Schema(description = "操作日志信息")
public class OperLogVO {

    @Schema(description = "日志主键")
    private Long operId;

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "业务类型 0=其它 1=新增 2=修改 3=删除")
    private Integer businessType;

    @Schema(description = "方法名称")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "操作人员")
    private String operName;

    @Schema(description = "请求URL")
    private String operUrl;

    @Schema(description = "主机地址")
    private String operIp;

    @Schema(description = "操作地点")
    private String operLocation;

    @Schema(description = "请求参数")
    private String operParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;

    @Schema(description = "操作状态 0=正常 1=失败")
    private Integer status;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "消耗时间（毫秒）")
    private Integer costTime;
}
