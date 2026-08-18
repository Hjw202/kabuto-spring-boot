package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新个人资料请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UpdateProfileDto 实现个人资料编辑请求参数</p>
 * <p><b>解决方案：</b>个人资料 DTO，仅含昵称、邮箱、性别、手机号</p>
 * <p><b>原因说明：</b>对应 nest-admin UpdateProfileDto（PickType UpdateUserDto ['name', 'email', 'sex', 'phone']）</p>
 */
@Data
@Schema(description = "更新个人资料请求")
public class UpdateProfileDTO {

    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 邮箱 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Schema(description = "邮箱", example = "zhangsan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /** 性别 0=女 1=男 */
    @Schema(description = "性别 0=女 1=男", example = "1")
    private Integer sex;

    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    @Size(max = 11, message = "手机号长度不能超过11个字符")
    @Schema(description = "手机号", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
}
