package com.kabuto.cloud.vo.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 授权激活响应 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 激活接口返回结构实现</p>
 * <p><b>解决方案：</b>封装激活成功后的响应数据：激活时间、过期时间、计划类型、签名</p>
 * <p><b>原因说明：</b>客户端需要用公钥+签名验证授权有效性</p>
 */
@Data
@Schema(description = "授权激活响应")
public class LicenseActivateVO {

    @Schema(description = "激活时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedAt;

    @Schema(description = "过期时间（null 表示永久）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    @Schema(description = "计划类型")
    private String planType;

    @Schema(description = "ECDSA 签名（Base64）")
    private String signature;
}
