package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin ResetPwdDto 实现重置密码请求参数</p>
 * <p><b>解决方案：</b>管理员重置用户密码的 DTO</p>
 * <p><b>原因说明：</b>对应 nest-admin ResetPwdDto。管理员可直接重置任意用户密码，无需旧密码</p>
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPwdDTO {

    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "新密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
