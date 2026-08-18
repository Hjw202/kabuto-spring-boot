package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新系统配置请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UpdateConfigDto 实现编辑配置请求参数</p>
 * <p><b>解决方案：</b>继承 CreateConfigDTO 并添加 configId</p>
 * <p><b>原因说明：</b>对应 nest-admin UpdateConfigDto</p>
 */
@Data
@Schema(description = "更新系统配置请求")
public class UpdateConfigDTO {

    /** 参数ID */
    @NotNull(message = "参数ID不能为空")
    @Schema(description = "参数ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /** 参数名称 */
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称长度不能超过100个字符")
    @Schema(description = "参数名称", example = "IP黑名单", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configName;

    /** 参数键名 */
    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名长度不能超过100个字符")
    @Schema(description = "参数键名", example = "sys.login.blackIPList", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configKey;

    /** 参数键值 */
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过500个字符")
    @Schema(description = "参数键值", example = "127.0.0.1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configValue;

    /** 是否系统内置 0=否 1=是 */
    @Schema(description = "是否系统内置 0=否 1=是", example = "0")
    private Integer configType;
}
