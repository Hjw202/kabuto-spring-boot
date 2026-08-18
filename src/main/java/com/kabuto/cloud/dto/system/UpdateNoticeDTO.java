package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新公告请求 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UpdateNoticeDto 实现编辑公告请求参数</p>
 * <p><b>解决方案：</b>继承 CreateNoticeDTO 并添加 noticeId</p>
 * <p><b>原因说明：</b>对应 nest-admin UpdateNoticeDto</p>
 */
@Data
@Schema(description = "更新公告请求")
public class UpdateNoticeDTO {

    /** 公告ID */
    @NotNull(message = "公告ID不能为空")
    @Schema(description = "公告ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /** 公告标题 */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题长度不能超过50个字符")
    @Schema(description = "公告标题", example = "系统维护通知", requiredMode = Schema.RequiredMode.REQUIRED)
    private String noticeTitle;

    /** 公告类型 1=通知 2=公告 */
    @NotNull(message = "公告类型不能为空")
    @Schema(description = "公告类型 1=通知 2=公告", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer noticeType;

    /** 公告内容 */
    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String noticeContent;

    /** 公告状态 0=关闭 1=正常 */
    @Schema(description = "公告状态 0=关闭 1=正常", example = "1")
    private Integer status;
}
