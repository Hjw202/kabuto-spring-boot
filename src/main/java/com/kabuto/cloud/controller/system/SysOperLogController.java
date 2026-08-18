package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.SearchOperLogDTO;
import com.kabuto.cloud.service.system.SysOperLogService;
import com.kabuto.cloud.vo.system.OperLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 *
 * <p><b>需求描述：</b>大王要求实现操作日志管理接口</p>
 * <p><b>解决方案：</b>实现操作日志分页查询、清空、批量删除等接口</p>
 * <p><b>原因说明：</b>接口路径：/v1/system/oper-log/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/oper-log")
@Tag(name = "操作日志", description = "操作日志管理相关接口")
public class SysOperLogController {

    private final SysOperLogService operLogService;

    public SysOperLogController(SysOperLogService operLogService) {
        this.operLogService = operLogService;
    }

    /**
     * 分页查询操作日志
     */
    @Operation(summary = "分页查询操作日志")
    @GetMapping("/")
    public R<PageResult<OperLogVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchOperLogDTO dto) {
        return operLogService.page(pageNum, pageSize, dto);
    }

    /**
     * 批量删除操作日志
     */
    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return operLogService.deleteOperLogs(ids);
    }

    /**
     * 清空操作日志
     */
    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clear")
    public R<Void> clear() {
        return operLogService.clear();
    }
}
