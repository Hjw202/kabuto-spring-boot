package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateNoticeDTO;
import com.kabuto.cloud.dto.system.SearchNoticeDTO;
import com.kabuto.cloud.dto.system.UpdateNoticeDTO;
import com.kabuto.cloud.vo.system.NoticeVO;

import java.util.List;

/**
 * 公告管理服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin NoticeService 实现公告管理服务层</p>
 * <p><b>解决方案：</b>定义公告 CRUD 服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin NoticeService</p>
 */
public interface SysNoticeService {

    /**
     * 分页查询公告列表
     */
    R<PageResult<NoticeVO>> page(Integer pageNum, Integer pageSize, SearchNoticeDTO dto);

    /**
     * 查询公告详情
     */
    R<NoticeVO> getNoticeById(Long id);

    /**
     * 创建公告
     */
    R<Void> createNotice(CreateNoticeDTO dto);

    /**
     * 更新公告
     */
    R<Void> updateNotice(Long id, UpdateNoticeDTO dto);

    /**
     * 批量删除公告
     */
    R<Void> deleteNotices(List<Long> ids);
}
