package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dto.system.CreateDictDataDTO;
import com.kabuto.cloud.dto.system.SearchDictDataDTO;
import com.kabuto.cloud.dto.system.UpdateDictDataDTO;
import com.kabuto.cloud.vo.system.DictDataVO;

import java.util.List;

/**
 * 字典数据服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictDataService 实现字典数据服务层</p>
 * <p><b>解决方案：</b>定义字典数据 CRUD、Redis 缓存等服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin DictDataService</p>
 */
public interface SysDictDataService {

    /**
     * 分页查询字典数据
     */
    PageResult<DictDataVO> page(Integer pageNum, Integer pageSize, SearchDictDataDTO dto);

    /**
     * 根据字典类型查询字典数据（优先从 Redis 缓存读取）
     */
    List<DictDataVO> getDictsByType(String dictType);

    /**
     * 查询字典数据详情
     */
    DictDataVO getDictDataById(Long id);

    /**
     * 创建字典数据（更新 Redis 缓存）
     */
    void createDictData(CreateDictDataDTO dto);

    /**
     * 更新字典数据（更新 Redis 缓存）
     */
    void updateDictData(Long id, UpdateDictDataDTO dto);

    /**
     * 批量删除字典数据（更新 Redis 缓存）
     */
    void deleteDictData(List<Long> ids);

    /**
     * 刷新指定字典类型的 Redis 缓存
     */
    void refreshCacheByType(String dictType);

    /**
     * 清空所有字典缓存
     */
    void clearDictCache();
}
