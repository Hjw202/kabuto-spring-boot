package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建用户请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin CreateUserDto 实现创建用户请求参数</p>
 * <p><b>解决方案：</b>创建用户 DTO，使用 Jakarta Validation 注解做参数校验</p>
 * <p><b>原因说明：</b>对应 nest-admin CreateUserDto。字段包含账号、密码、昵称、手机、邮箱、性别、状态、角色</p>
 */
@Data
@Schema(description = "创建用户请求")
public class CreateUserDTO {

    /** 账号 */
    @NotBlank(message = "账号不能为空")
    @Size(max = 30, message = "账号长度不能超过30个字符")
    @Schema(description = "账号", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    @Size(max = 11, message = "手机号长度不能超过11个字符")
    @Schema(description = "手机号", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    /** 邮箱 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Schema(description = "邮箱", example = "zhangsan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /** 性别 0=女 1=男 */
    @Schema(description = "性别 0=女 1=男", example = "1")
    private Integer sex = 1;

    /** 状态 0=禁用 1=正常 */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态 0=禁用 1=正常", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    /** 角色ID列表 */
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
