package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateDictTypeDTO;
import com.kabuto.cloud.dto.system.SearchDictTypeDTO;
import com.kabuto.cloud.dto.system.UpdateDictTypeDTO;
import com.kabuto.cloud.vo.system.DictTypeVO;

import java.util.List;

/**
 * 字典类型服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictTypeService 实现字典类型服务层</p>
 * <p><b>解决方案：</b>定义字典类型 CRUD、缓存刷新等服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin DictTypeService</p>
 */
public interface SysDictTypeService {

    /**
     * 分页查询字典类型
     */
    R<PageResult<DictTypeVO>> page(Integer pageNum, Integer pageSize, SearchDictTypeDTO dto);

    /**
     * 查询所有启用的字典类型（下拉选择用）
     */
    R<List<DictTypeVO>> optionselect();

    /**
     * 查询字典类型详情
     */
    R<DictTypeVO> getDictTypeById(Long id);

    /**
     * 创建字典类型
     */
    R<Void> createDictType(CreateDictTypeDTO dto);

    /**
     * 更新字典类型（事务：若 dictType 键变更，同步更新关联的 dictData）
     */
    R<Void> updateDictType(Long id, UpdateDictTypeDTO dto);

    /**
     * 批量删除字典类型（校验无子 dictData 记录后删除）
     */
    R<Void> deleteDictTypes(List<Long> ids);

    /**
     * 刷新字典缓存（清空并重建）
     */
    R<Void> refreshDictCache();
}
