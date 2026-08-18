package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateDictTypeDTO;
import com.kabuto.cloud.dto.system.SearchDictTypeDTO;
import com.kabuto.cloud.dto.system.UpdateDictTypeDTO;
import com.kabuto.cloud.service.system.SysDictTypeService;
import com.kabuto.cloud.vo.system.DictTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictTypeController 实现字典类型管理接口</p>
 * <p><b>解决方案：</b>实现字典类型 CRUD、缓存刷新等接口</p>
 * <p><b>原因说明：</b>对应 nest-admin DictTypeController。接口路径：/v1/system/dict/type/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/dict/type")
@Tag(name = "字典类型", description = "字典类型管理相关接口")
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    public SysDictTypeController(SysDictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    /**
     * 下拉选择字典类型
     */
    @Operation(summary = "下拉选择字典类型")
    @GetMapping("/optionselect")
    public R<List<DictTypeVO>> optionselect() {
        return R.ok(dictTypeService.optionselect());
    }

    /**
     * 分页查询字典类型
     */
    @Operation(summary = "分页查询字典类型")
    @RequirePermission("system:dict:query")
    @GetMapping("/")
    public R<PageResult<DictTypeVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchDictTypeDTO dto) {
        return R.ok(dictTypeService.page(pageNum, pageSize, dto));
    }

    /**
     * 查询字典类型详情
     */
    @Operation(summary = "查询字典类型详情")
    @GetMapping("/{id}")
    public R<DictTypeVO> detail(@PathVariable Long id) {
        return R.ok(dictTypeService.getDictTypeById(id));
    }

    /**
     * 创建字典类型
     */
    @Operation(summary = "创建字典类型")
    @RequirePermission("system:dict:add")
    @PostMapping("/")
    public R<Void> create(@Valid @RequestBody CreateDictTypeDTO dto) {
        dictTypeService.createDictType(dto);
        return R.ok();
    }

    /**
     * 更新字典类型
     */
    @Operation(summary = "更新字典类型")
    @RequirePermission("system:dict:edit")
    @PutMapping("/")
    public R<Void> update(@Valid @RequestBody UpdateDictTypeDTO dto) {
        dictTypeService.updateDictType(dto.getId(), dto);
        return R.ok();
    }

    /**
     * 刷新字典缓存
     */
    @Operation(summary = "刷新字典缓存")
    @RequirePermission("system:dict:edit")
    @GetMapping("/refreshCache")
    public R<Void> refreshCache() {
        dictTypeService.refreshDictCache();
        return R.ok();
    }

    /**
     * 删除字典类型
     */
    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        dictTypeService.deleteDictTypes(ids);
        return R.ok();
    }
}
