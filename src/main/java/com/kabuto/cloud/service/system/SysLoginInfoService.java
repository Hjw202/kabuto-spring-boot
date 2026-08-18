package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dto.system.SearchLoginInfoDTO;
import com.kabuto.cloud.vo.system.LoginInfoVO;

import java.util.List;

/**
 * 登录日志服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LoginInfoService 实现登录日志服务层</p>
 * <p><b>解决方案：</b>定义登录日志分页查询、清空、批量删除等服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin LoginInfoService。登录日志由登录流程自动写入，无需创建接口</p>
 */
public interface SysLoginInfoService {

    /**
     * 分页查询登录日志
     */
    PageResult<LoginInfoVO> page(Integer pageNum, Integer pageSize, SearchLoginInfoDTO dto);

    /**
     * 批量删除登录日志
     */
    void deleteLoginInfos(List<Long> ids);

    /**
     * 清空登录日志（TRUNCATE 表）
     */
    void clear();
}
