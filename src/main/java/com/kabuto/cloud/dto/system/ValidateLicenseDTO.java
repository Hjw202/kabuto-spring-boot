package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 验证授权码请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin ValidateLicenseDto 实现授权验证请求参数</p>
 * <p><b>解决方案：</b>客户端验证授权码有效性，提交授权码+设备指纹</p>
 * <p><b>原因说明：</b>对应 nest-admin ValidateLicenseDto</p>
 */
@Data
@Schema(description = "验证授权码请求")
public class ValidateLicenseDTO {

    /** 授权码 */
    @NotBlank(message = "授权码不能为空")
    @Size(max = 64, message = "授权码长度不能超过64个字符")
    @Schema(description = "授权码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String licenseCode;

    /** 设备指纹 SHA-256 */
    @NotBlank(message = "设备指纹不能为空")
    @Size(max = 128, message = "设备指纹长度不能超过128个字符")
    @Schema(description = "设备指纹 SHA-256", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deviceFingerprint;
}
