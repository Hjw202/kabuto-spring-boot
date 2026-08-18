package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.SearchOperLogDTO;
import com.kabuto.cloud.vo.system.OperLogVO;

import java.util.List;

/**
 * 操作日志服务接口
 *
 * <p><b>需求描述：</b>大王要求实现操作日志管理服务层</p>
 * <p><b>解决方案：</b>定义操作日志分页查询、清空、批量删除等服务接口</p>
 * <p><b>原因说明：</b>操作日志记录后台操作行为，用于审计和问题追踪。后续可通过 AOP 自动记录</p>
 */
public interface SysOperLogService {

    /**
     * 分页查询操作日志
     */
    R<PageResult<OperLogVO>> page(Integer pageNum, Integer pageSize, SearchOperLogDTO dto);

    /**
     * 批量删除操作日志
     */
    R<Void> deleteOperLogs(List<Long> ids);

    /**
     * 清空操作日志（TRUNCATE 表）
     */
    R<Void> clear();
}
