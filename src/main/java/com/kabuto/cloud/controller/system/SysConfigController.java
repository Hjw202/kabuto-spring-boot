package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateConfigDTO;
import com.kabuto.cloud.dto.system.SearchConfigDTO;
import com.kabuto.cloud.dto.system.UpdateConfigDTO;
import com.kabuto.cloud.service.system.SysConfigService;
import com.kabuto.cloud.vo.system.ConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin ConfigController 实现系统配置管理接口</p>
 * <p><b>解决方案：</b>实现配置 CRUD、缓存刷新等接口</p>
 * <p><b>原因说明：</b>对应 nest-admin ConfigController。接口路径：/v1/system/config/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/config")
@Tag(name = "配置管理", description = "系统配置管理相关接口")
public class SysConfigController {

    private final SysConfigService configService;

    public SysConfigController(SysConfigService configService) {
        this.configService = configService;
    }

    /**
     * 分页查询配置列表
     */
    @Operation(summary = "分页查询配置列表")
    @RequirePermission("system:config:query")
    @GetMapping("/")
    public R<PageResult<ConfigVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchConfigDTO dto) {
        return configService.page(pageNum, pageSize, dto);
    }

    /**
     * 查询配置详情
     */
    @Operation(summary = "查询配置详情")
    @RequirePermission("system:config:query")
    @GetMapping("/{configId}")
    public R<ConfigVO> detail(@PathVariable Long configId) {
        return configService.getConfigById(configId);
    }

    /**
     * 创建配置
     */
    @Operation(summary = "创建配置")
    @RequirePermission("system:config:add")
    @PostMapping("/")
    public R<Void> create(@Valid @RequestBody CreateConfigDTO dto) {
        return configService.createConfig(dto);
    }

    /**
     * 更新配置
     */
    @Operation(summary = "更新配置")
    @RequirePermission("system:config:edit")
    @PutMapping("/")
    public R<Void> update(@Valid @RequestBody UpdateConfigDTO dto) {
        return configService.updateConfig(dto);
    }

    /**
     * 刷新配置缓存
     */
    @Operation(summary = "刷新配置缓存")
    @RequirePermission("system:config:edit")
    @DeleteMapping("/refreshCache")
    public R<Void> refreshCache() {
        return configService.refreshCache();
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置")
    @RequirePermission("system:config:remove")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return configService.deleteConfigs(ids);
    }
}
