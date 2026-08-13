package com.kabuto.cloud.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin getInfo 接口响应实现用户信息 VO</p>
 * <p><b>解决方案：</b>封装用户详细信息，包括角色和权限</p>
 * <p><b>原因说明：</b>对应 nest-admin getInfo 接口返回的用户信息结构。
 * 该数据会被缓存到 Redis 中，减少重复查询</p>
 */
@Data
@Schema(description = "用户信息")
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "昵称")
    private String name;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "性别 0=女 1=男")
    private Integer sex;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态 0=禁用 1=正常")
    private Integer status;

    @Schema(description = "角色Key列表")
    private List<String> roles;

    @Schema(description = "权限标识列表")
    private List<String> permissions;
}
