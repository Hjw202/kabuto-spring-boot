package com.kabuto.cloud.vo.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告信息 VO
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin NoticeVO 实现公告详情响应</p>
 * <p><b>解决方案：</b>封装公告详细信息</p>
 * <p><b>原因说明：</b>对应 nest-admin NoticeVO</p>
 */
@Data
@Schema(description = "公告信息")
public class NoticeVO {

    @Schema(description = "公告ID")
    private Long noticeId;

    @Schema(description = "公告标题")
    private String noticeTitle;

    @Schema(description = "公告类型 1=通知 2=公告")
    private Integer noticeType;

    @Schema(description = "公告内容")
    private String noticeContent;

    @Schema(description = "公告状态 0=关闭 1=正常")
    private Integer status;

    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
