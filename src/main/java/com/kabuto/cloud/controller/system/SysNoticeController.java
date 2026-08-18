package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateNoticeDTO;
import com.kabuto.cloud.dto.system.SearchNoticeDTO;
import com.kabuto.cloud.dto.system.UpdateNoticeDTO;
import com.kabuto.cloud.service.system.SysNoticeService;
import com.kabuto.cloud.vo.system.NoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告管理控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin NoticeController 实现公告管理接口</p>
 * <p><b>解决方案：</b>实现公告 CRUD 接口</p>
 * <p><b>原因说明：</b>对应 nest-admin NoticeController。接口路径：/v1/system/notice/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/notice")
@Tag(name = "通知公告", description = "通知公告管理相关接口")
public class SysNoticeController {

    private final SysNoticeService noticeService;

    public SysNoticeController(SysNoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * 分页查询公告列表
     */
    @Operation(summary = "分页查询公告列表")
    @RequirePermission("system:notice:query")
    @GetMapping("/")
    public R<PageResult<NoticeVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchNoticeDTO dto) {
        return noticeService.page(pageNum, pageSize, dto);
    }

    /**
     * 查询公告详情
     */
    @Operation(summary = "查询公告详情")
    @RequirePermission("system:notice:query")
    @GetMapping("/{id}")
    public R<NoticeVO> detail(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    /**
     * 创建公告
     */
    @Operation(summary = "创建公告")
    @RequirePermission("system:notice:add")
    @PostMapping("/")
    public R<Void> create(@Valid @RequestBody CreateNoticeDTO dto) {
        return noticeService.createNotice(dto);
    }

    /**
     * 更新公告
     */
    @Operation(summary = "更新公告")
    @RequirePermission("system:notice:edit")
    @PutMapping("/")
    public R<Void> update(@Valid @RequestBody UpdateNoticeDTO dto) {
        return noticeService.updateNotice(dto.getId(), dto);
    }

    /**
     * 删除公告
     */
    @Operation(summary = "删除公告")
    @RequirePermission("system:notice:remove")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return noticeService.deleteNotices(ids);
    }
}
