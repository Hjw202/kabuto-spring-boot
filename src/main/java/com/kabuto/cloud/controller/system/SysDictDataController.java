package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateDictDataDTO;
import com.kabuto.cloud.dto.system.SearchDictDataDTO;
import com.kabuto.cloud.dto.system.UpdateDictDataDTO;
import com.kabuto.cloud.service.system.SysDictDataService;
import com.kabuto.cloud.vo.system.DictDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictDataController 实现字典数据管理接口</p>
 * <p><b>解决方案：</b>实现字典数据 CRUD、按类型查询等接口</p>
 * <p><b>原因说明：</b>对应 nest-admin DictDataController。接口路径：/v1/system/dict/data/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/dict/data")
@Tag(name = "字典数据", description = "字典数据管理相关接口")
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    public SysDictDataController(SysDictDataService dictDataService) {
        this.dictDataService = dictDataService;
    }

    /**
     * 分页查询字典数据
     */
    @Operation(summary = "分页查询字典数据")
    @RequirePermission("system:dict:query")
    @GetMapping("/")
    public R<PageResult<DictDataVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchDictDataDTO dto) {
        return dictDataService.page(pageNum, pageSize, dto);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @Operation(summary = "根据字典类型查询字典数据")
    @GetMapping("/type/{type}")
    public R<List<DictDataVO>> getDicts(@PathVariable String type) {
        return dictDataService.getDictsByType(type);
    }

    /**
     * 查询字典数据详情
     */
    @Operation(summary = "查询字典数据详情")
    @RequirePermission("system:dict:query")
    @GetMapping("/{id}")
    public R<DictDataVO> detail(@PathVariable Long id) {
        return dictDataService.getDictDataById(id);
    }

    /**
     * 创建字典数据
     */
    @Operation(summary = "创建字典数据")
    @RequirePermission("system:dict:add")
    @PostMapping("/")
    public R<Void> create(@Valid @RequestBody CreateDictDataDTO dto) {
        return dictDataService.createDictData(dto);
    }

    /**
     * 更新字典数据
     */
    @Operation(summary = "更新字典数据")
    @RequirePermission("system:dict:edit")
    @PutMapping("/")
    public R<Void> update(@Valid @RequestBody UpdateDictDataDTO dto) {
        return dictDataService.updateDictData(dto.getId(), dto);
    }

    /**
     * 删除字典数据
     */
    @Operation(summary = "删除字典数据")
    @RequirePermission("system:dict:remove")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return dictDataService.deleteDictData(ids);
    }
}
