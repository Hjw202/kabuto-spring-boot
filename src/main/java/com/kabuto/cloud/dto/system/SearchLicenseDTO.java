package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 授权码搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求实现授权码搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>管理端需要按状态和计划类型筛选授权码</p>
 */
@Data
@Schema(description = "授权码搜索条件")
public class SearchLicenseDTO {

    /** 授权状态 0=未使用 1=已激活 2=已过期 3=已禁用 */
    @Schema(description = "授权状态")
    private Integer status;

    /** 计划类型 trial/basic/pro/enterprise/permanent */
    @Schema(description = "计划类型")
    private String planType;
}
