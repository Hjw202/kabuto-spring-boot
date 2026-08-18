package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin ConfigVO 实现配置详情响应</p>
 * <p><b>解决方案：</b>封装系统配置详细信息</p>
 * <p><b>原因说明：</b>对应 nest-admin ConfigVO</p>
 */
@Data
@Schema(description = "系统配置信息")
public class ConfigVO {

    @Schema(description = "参数ID")
    private Long configId;

    @Schema(description = "参数名称")
    private String configName;

    @Schema(description = "参数键名")
    private String configKey;

    @Schema(description = "参数键值")
    private String configValue;

    @Schema(description = "是否系统内置 0=否 1=是")
    private Integer configType;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
