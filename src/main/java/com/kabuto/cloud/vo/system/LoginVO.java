package com.kabuto.cloud.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录响应 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 登录响应实现 Java 版本</p>
 * <p><b>解决方案：</b>封装登录成功后的响应数据：用户信息、Token、权限列表</p>
 * <p><b>原因说明：</b>对应 nest-admin 登录接口返回的 { user, token, permissions } 结构</p>
 */
@Data
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "用户信息")
    private UserInfo user;

    @Schema(description = "JWT Token")
    private String token;

    @Schema(description = "权限标识列表")
    private List<String> permissions;

    /**
     * 登录响应中的用户基本信息
     */
    @Data
    @Schema(description = "登录用户信息")
    public static class UserInfo {

        @Schema(description = "用户ID")
        private Long id;

        @Schema(description = "账号")
        private String username;

        @Schema(description = "昵称")
        private String name;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "手机号")
        private String phone;

        @Schema(description = "状态 0=禁用 1=正常")
        private Integer status;

        @Schema(description = "角色ID列表")
        private List<Long> roles;
    }
}
