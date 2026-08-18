package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UserVO 实现用户详情响应</p>
 * <p><b>解决方案：</b>封装用户详细信息，包含角色ID列表，排除密码和权限（权限通过 getInfo 接口获取）</p>
 * <p><b>原因说明：</b>对应 nest-admin UserVO（OmitType UserEntity ['password', 'permissions']）。
 * 使用 roleIds 替代 roles 实体列表，避免循环引用</p>
 */
@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "昵称")
    private String name;

    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

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

    @Schema(description = "上次登录IP")
    private String loginIp;

    @Schema(description = "上次登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginDate;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
