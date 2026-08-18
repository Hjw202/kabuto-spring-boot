package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dao.system.SysNoticeMapper;
import com.kabuto.cloud.dto.system.CreateNoticeDTO;
import com.kabuto.cloud.dto.system.SearchNoticeDTO;
import com.kabuto.cloud.dto.system.UpdateNoticeDTO;
import com.kabuto.cloud.entity.system.SysNotice;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.SysNoticeService;
import com.kabuto.cloud.vo.system.NoticeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告管理服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin NoticeService 实现公告管理业务逻辑</p>
 * <p><b>解决方案：</b>实现公告 CRUD 核心功能，无缓存逻辑</p>
 * <p><b>原因说明：</b>对应 nest-admin NoticeService。公告为纯 CRUD 模块，无特殊业务逻辑</p>
 */
@Slf4j
@Service
public class SysNoticeServiceImpl implements SysNoticeService {

    private final SysNoticeMapper noticeMapper;

    public SysNoticeServiceImpl(SysNoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    @Override
    public PageResult<NoticeVO> page(Integer pageNum, Integer pageSize, SearchNoticeDTO dto) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getNoticeTitle()), SysNotice::getNoticeTitle, dto.getNoticeTitle())
                .like(StringUtils.hasText(dto.getCreateBy()), SysNotice::getCreateBy, dto.getCreateBy())
                .eq(dto.getNoticeType() != null, SysNotice::getNoticeType, dto.getNoticeType())
                .orderByAsc(SysNotice::getCreateTime);

        Page<SysNotice> page = noticeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<NoticeVO> voList = page.getRecords().stream()
                .map(this::convertToNoticeVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public NoticeVO getNoticeById(Long id) {
        SysNotice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BizException(ResultCode.NOT_FOUND, "公告不存在");
        }
        return convertToNoticeVO(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNotice(CreateNoticeDTO dto) {
        SysNotice notice = new SysNotice();
        notice.setNoticeTitle(dto.getNoticeTitle());
        notice.setNoticeType(dto.getNoticeType());
        notice.setNoticeContent(dto.getNoticeContent());
        notice.setStatus(dto.getStatus());
        notice.setPublishTime(LocalDateTime.now());
        notice.setCreateBy("admin");
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);

        log.info("[创建公告] noticeId={}, title={}", notice.getNoticeId(), notice.getNoticeTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(Long id, UpdateNoticeDTO dto) {
        SysNotice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BizException(ResultCode.NOT_FOUND, "公告不存在");
        }

        notice.setNoticeTitle(dto.getNoticeTitle());
        notice.setNoticeType(dto.getNoticeType());
        notice.setNoticeContent(dto.getNoticeContent());
        if (dto.getStatus() != null) {
            notice.setStatus(dto.getStatus());
        }
        notice.setUpdateBy("admin");
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);

        log.info("[更新公告] noticeId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotices(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        for (Long id : ids) {
            SysNotice notice = noticeMapper.selectById(id);
            if (notice == null) {
                continue;
            }
            noticeMapper.deleteById(id);
        }

        log.info("[批量删除公告] ids={}", ids);
    }

    // ==================== 私有辅助方法 ====================

    private NoticeVO convertToNoticeVO(SysNotice notice) {
        NoticeVO vo = new NoticeVO();
        vo.setNoticeId(notice.getNoticeId());
        vo.setNoticeTitle(notice.getNoticeTitle());
        vo.setNoticeType(notice.getNoticeType());
        vo.setNoticeContent(notice.getNoticeContent());
        vo.setStatus(notice.getStatus());
        vo.setPublishTime(notice.getPublishTime());
        vo.setEndTime(notice.getEndTime());
        vo.setCreateBy(notice.getCreateBy());
        vo.setCreateTime(notice.getCreateTime());
        return vo;
    }
}
