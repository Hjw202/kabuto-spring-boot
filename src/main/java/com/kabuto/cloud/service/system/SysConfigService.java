package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateConfigDTO;
import com.kabuto.cloud.dto.system.SearchConfigDTO;
import com.kabuto.cloud.dto.system.UpdateConfigDTO;
import com.kabuto.cloud.vo.system.ConfigVO;

import java.util.List;

/**
 * 系统配置服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin ConfigService 实现系统配置服务层</p>
 * <p><b>解决方案：</b>定义配置 CRUD、Redis 缓存等服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin ConfigService</p>
 */
public interface SysConfigService {

    /**
     * 分页查询配置列表
     */
    R<PageResult<ConfigVO>> page(Integer pageNum, Integer pageSize, SearchConfigDTO dto);

    /**
     * 查询配置详情
     */
    R<ConfigVO> getConfigById(Long id);

    /**
     * 创建配置（同步写入 Redis 缓存）
     */
    R<Void> createConfig(CreateConfigDTO dto);

    /**
     * 更新配置（同步更新 Redis 缓存）
     */
    R<Void> updateConfig(UpdateConfigDTO dto);

    /**
     * 批量删除配置（禁止删除系统内置配置）
     */
    R<Void> deleteConfigs(List<Long> ids);

    /**
     * 刷新配置缓存（清空并重建）
     */
    R<Void> refreshCache();

    /**
     * 启动时加载配置缓存
     */
    void loadingConfigCache();
}
