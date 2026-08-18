package com.kabuto.cloud.vo.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 授权码信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LicenseVO 实现授权码详情响应</p>
 * <p><b>解决方案：</b>封装授权码详细信息，排除签名字段</p>
 * <p><b>原因说明：</b>对应 nest-admin LicenseVO。signature 字段不返回前端（仅用于验证）</p>
 */
@Data
@Schema(description = "授权码信息")
public class LicenseVO {

    @Schema(description = "授权码ID")
    private Long licenseId;

    @Schema(description = "授权码")
    private String licenseCode;

    @Schema(description = "计划类型")
    private String planType;

    @Schema(description = "最大设备数")
    private Integer maxDevices;

    @Schema(description = "授权状态 0=未使用 1=已激活 2=已过期 3=已禁用")
    private Integer status;

    @Schema(description = "激活时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedAt;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
