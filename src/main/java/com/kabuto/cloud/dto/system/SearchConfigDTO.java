package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchConfigDto 实现配置搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchConfigDto</p>
 */
@Data
@Schema(description = "系统配置搜索条件")
public class SearchConfigDTO {

    /** 参数名称 */
    @Schema(description = "参数名称")
    private String configName;

    /** 参数键名 */
    @Schema(description = "参数键名")
    private String configKey;

    /** 参数键值 */
    @Schema(description = "参数键值")
    private String configValue;

    /** 是否系统内置 0=否 1=是 */
    @Schema(description = "是否系统内置")
    private Integer configType;
}
