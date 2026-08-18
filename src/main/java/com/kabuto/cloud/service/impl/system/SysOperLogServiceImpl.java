package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dao.system.SysOperLogMapper;
import com.kabuto.cloud.dto.system.SearchOperLogDTO;
import com.kabuto.cloud.entity.system.SysOperLog;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.SysOperLogService;
import com.kabuto.cloud.vo.system.OperLogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.StringReader;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 *
 * <p><b>需求描述：</b>大王要求实现操作日志管理业务逻辑</p>
 * <p><b>解决方案：</b>实现操作日志分页查询、批量删除、清空（TRUNCATE）功能</p>
 * <p><b>原因说明：</b>操作日志由 AOP 切面自动写入，本服务仅提供查询和清理能力</p>
 */
@Slf4j
@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogMapper operLogMapper;
    private final DataSource dataSource;

    public SysOperLogServiceImpl(SysOperLogMapper operLogMapper, DataSource dataSource) {
        this.operLogMapper = operLogMapper;
        this.dataSource = dataSource;
    }

    @Override
    public PageResult<OperLogVO> page(Integer pageNum, Integer pageSize, SearchOperLogDTO dto) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getTitle()), SysOperLog::getTitle, dto.getTitle())
                .eq(dto.getBusinessType() != null, SysOperLog::getBusinessType, dto.getBusinessType())
                .like(StringUtils.hasText(dto.getOperName()), SysOperLog::getOperName, dto.getOperName())
                .eq(dto.getStatus() != null, SysOperLog::getStatus, dto.getStatus())
                .ge(dto.getStartTime() != null, SysOperLog::getOperTime, dto.getStartTime())
                .le(dto.getEndTime() != null, SysOperLog::getOperTime, dto.getEndTime())
                .orderByDesc(SysOperLog::getOperTime);

        Page<SysOperLog> page = operLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<OperLogVO> voList = page.getRecords().stream()
                .map(this::convertToOperLogVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public void deleteOperLogs(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            operLogMapper.deleteById(id);
        }
        log.info("[批量删除操作日志] ids={}", ids);
    }

    @Override
    public void clear() {
        try (Connection conn = dataSource.getConnection()) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.runScript(new StringReader("TRUNCATE TABLE sys_oper_log"));
            log.info("[清空操作日志]");
        } catch (Exception e) {
            log.error("[清空操作日志失败] error={}", e.getMessage(), e);
            throw new BizException("清空失败");
        }
    }

    // ==================== 私有辅助方法 ====================

    private OperLogVO convertToOperLogVO(SysOperLog operLog) {
        OperLogVO vo = new OperLogVO();
        vo.setOperId(operLog.getOperId());
        vo.setTitle(operLog.getTitle());
        vo.setBusinessType(operLog.getBusinessType());
        vo.setMethod(operLog.getMethod());
        vo.setRequestMethod(operLog.getRequestMethod());
        vo.setOperName(operLog.getOperName());
        vo.setOperUrl(operLog.getOperUrl());
        vo.setOperIp(operLog.getOperIp());
        vo.setOperLocation(operLog.getOperLocation());
        vo.setOperParam(operLog.getOperParam());
        vo.setJsonResult(operLog.getJsonResult());
        vo.setOperTime(operLog.getOperTime());
        vo.setStatus(operLog.getStatus());
        vo.setErrorMsg(operLog.getErrorMsg());
        vo.setCostTime(operLog.getCostTime());
        return vo;
    }
}
