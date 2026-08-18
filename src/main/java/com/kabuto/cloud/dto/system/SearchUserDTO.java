package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchUserDto 实现用户搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchUserDto。支持按用户名、手机号、状态、时间范围筛选</p>
 */
@Data
@Schema(description = "用户搜索条件")
public class SearchUserDTO {

    /** 账号 */
    @Schema(description = "账号")
    private String username;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 状态 0=禁用 1=正常 */
    @Schema(description = "状态 0=禁用 1=正常")
    private Integer status;

    /** 开始时间 */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
