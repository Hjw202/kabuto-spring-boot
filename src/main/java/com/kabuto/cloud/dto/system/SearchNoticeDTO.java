package com.kabuto.cloud.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 公告搜索条件 DTO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin SearchNoticeDto 实现公告搜索条件</p>
 * <p><b>解决方案：</b>搜索条件 DTO，所有字段可选</p>
 * <p><b>原因说明：</b>对应 nest-admin SearchNoticeDto</p>
 */
@Data
@Schema(description = "公告搜索条件")
public class SearchNoticeDTO {

    /** 公告标题 */
    @Schema(description = "公告标题")
    private String noticeTitle;

    /** 创建者 */
    @Schema(description = "创建者")
    private String createBy;

    /** 公告类型 1=通知 2=公告 */
    @Schema(description = "公告类型 1=通知 2=公告")
    private Integer noticeType;
}
