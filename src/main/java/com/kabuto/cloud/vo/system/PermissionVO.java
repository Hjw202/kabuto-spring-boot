package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin PermissionVO 实现权限详情响应</p>
 * <p><b>解决方案：</b>封装权限详细信息</p>
 * <p><b>原因说明：</b>对应 nest-admin PermissionVO</p>
 */
@Data
@Schema(description = "权限信息")
public class PermissionVO {

    @Schema(description = "权限ID")
    private Long permissionId;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态 0=停用 1=正常")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
