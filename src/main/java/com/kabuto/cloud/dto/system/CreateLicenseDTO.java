package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建授权码请求 DTO（管理端）
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LicenseController 管理端创建接口实现</p>
 * <p><b>解决方案：</b>管理员创建授权码，指定计划类型、最大设备数、过期时间、备注</p>
 * <p><b>原因说明：</b>对应 nest-admin createLicense 方法的参数</p>
 */
@Data
@Schema(description = "创建授权码请求（管理端）")
public class CreateLicenseDTO {

    /** 计划类型 trial/basic/pro/enterprise/permanent */
    @NotNull(message = "计划类型不能为空")
    @Schema(description = "计划类型", example = "basic", requiredMode = Schema.RequiredMode.REQUIRED)
    private String planType;

    /** 最大设备数 */
    @Schema(description = "最大设备数", example = "1")
    private Integer maxDevices = 1;

    /** 过期时间（null 表示永久） */
    @Schema(description = "过期时间（null 表示永久）")
    private LocalDateTime expiresAt;

    /** 备注 */
    @Schema(description = "备注")
    private String note;
}
