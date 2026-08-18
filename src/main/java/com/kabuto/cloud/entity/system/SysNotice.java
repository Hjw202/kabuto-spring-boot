package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的 Java 实体类</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_notice 表</p>
 * <p><b>原因说明：</b>公告表用于管理系统通知和公告信息，支持定时发布和失效</p>
 */
@Data
@TableName("sys_notice")
public class SysNotice {

    /** 公告ID */
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;

    /** 公告标题 */
    @TableField("notice_title")
    private String noticeTitle;

    /** 公告类型 1=通知 2=公告 */
    @TableField("notice_type")
    private Integer noticeType;

    /** 公告内容 */
    @TableField("notice_content")
    private String noticeContent;

    /** 公告状态 0=关闭 1=正常 */
    private Integer status;

    /** 发布时间 */
    @TableField("publish_time")
    private LocalDateTime publishTime;

    /** 失效时间 */
    @TableField("end_time")
    private LocalDateTime endTime;

    /** 创建者 */
    @TableField("create_by")
    private String createBy;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField("update_by")
    private String updateBy;

    /** 更新时间 */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /** 逻辑删除 0=正常 1=删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
