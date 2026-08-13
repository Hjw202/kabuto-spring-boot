package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LoginUserDto 实现登录请求参数</p>
 * <p><b>解决方案：</b>创建登录 DTO，使用 Jakarta Validation 注解做参数校验</p>
 * <p><b>原因说明：</b>对应 nest-admin LoginUserDto。使用 @NotBlank 替代 @IsNotEmpty，
 * 更符合 Java 生态标准。Swagger @Schema 自动生成接口文档</p>
 */
@Data
@Schema(description = "用户登录请求")
public class LoginUserDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名或邮箱或手机号", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
