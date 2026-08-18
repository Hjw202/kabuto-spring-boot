package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchLoginInfoDto 实现登录日志搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchLoginInfoDto</p>
 */
@Data
@Schema(description = "登录日志搜索条件")
public class SearchLoginInfoDTO {

    /** 用户账号 */
    @Schema(description = "用户账号")
    private String account;

    /** IP地址 */
    @Schema(description = "IP地址")
    private String ipAddress;

    /** 登录状态 0=失败 1=成功 */
    @Schema(description = "登录状态 0=失败 1=成功")
    private Integer status;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
