package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LoginInfoVO 实现登录日志详情响应</p>
 * <p><b>解决方案：</b>封装登录日志详细信息</p>
 * <p><b>原因说明：</b>对应 nest-admin LoginInfoVO</p>
 */
@Data
@Schema(description = "登录日志信息")
public class LoginInfoVO {

    @Schema(description = "登录日志ID")
    private Long loginInfoId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "登录账号")
    private String account;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录状态 0=失败 1=成功")
    private Integer status;

    @Schema(description = "提示消息")
    private String msg;

    @Schema(description = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
}
