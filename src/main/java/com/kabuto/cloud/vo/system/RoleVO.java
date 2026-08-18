package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin RoleVO 实现角色详情响应</p>
 * <p><b>解决方案：</b>封装角色详细信息，包含关联的 menuIds 和 permissionIds</p>
 * <p><b>原因说明：</b>对应 nest-admin RoleVO。前端在编辑角色时需要回显已选的菜单和权限</p>
 */
@Data
@Schema(description = "角色信息")
public class RoleVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "权限字符")
    private String roleKey;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否超管 0=否 1=是")
    private Integer isAdmin;

    @Schema(description = "状态 0=禁用 1=正常")
    private Integer status;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;
}
