package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新用户请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UpdateUserDto 实现编辑用户请求参数</p>
 * <p><b>解决方案：</b>编辑用户 DTO，不含 username 和 password（账号密码不允许在此修改）</p>
 * <p><b>原因说明：</b>对应 nest-admin UpdateUserDto（OmitType CreateUserDto ['username', 'password']）</p>
 */
@Data
@Schema(description = "更新用户请求")
public class UpdateUserDTO {

    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 手机号 */
    @Size(max = 11, message = "手机号长度不能超过11个字符")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /** 性别 0=女 1=男 */
    @Schema(description = "性别 0=女 1=男", example = "1")
    private Integer sex;

    /** 状态 0=禁用 1=正常 */
    @Schema(description = "状态 0=禁用 1=正常", example = "1")
    private Integer status;

    /** 角色ID列表 */
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
