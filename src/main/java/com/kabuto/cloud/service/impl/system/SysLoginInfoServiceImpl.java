package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dao.system.SysLoginInfoMapper;
import com.kabuto.cloud.dto.system.SearchLoginInfoDTO;
import com.kabuto.cloud.entity.system.SysLoginInfo;
import com.kabuto.cloud.service.system.SysLoginInfoService;
import com.kabuto.cloud.vo.system.LoginInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LoginInfoService 实现登录日志业务逻辑</p>
 * <p><b>解决方案：</b>实现登录日志分页查询、批量删除、清空（TRUNCATE）功能</p>
 * <p><b>原因说明：</b>对应 nest-admin LoginInfoService。登录日志由 AuthServiceImpl 异步写入，
 * 本服务仅提供查询和清理能力</p>
 */
@Slf4j
@Service
public class SysLoginInfoServiceImpl implements SysLoginInfoService {

    private final SysLoginInfoMapper loginInfoMapper;
    private final DataSource dataSource;

    public SysLoginInfoServiceImpl(SysLoginInfoMapper loginInfoMapper, DataSource dataSource) {
        this.loginInfoMapper = loginInfoMapper;
        this.dataSource = dataSource;
    }

    @Override
    public R<PageResult<LoginInfoVO>> page(Integer pageNum, Integer pageSize, SearchLoginInfoDTO dto) {
        LambdaQueryWrapper<SysLoginInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(dto.getAccount()), SysLoginInfo::getAccount, dto.getAccount())
                .like(StringUtils.hasText(dto.getIpAddress()), SysLoginInfo::getIpAddress, dto.getIpAddress())
                .eq(dto.getStatus() != null, SysLoginInfo::getStatus, dto.getStatus())
                .ge(dto.getStartTime() != null, SysLoginInfo::getLoginTime, dto.getStartTime())
                .le(dto.getEndTime() != null, SysLoginInfo::getLoginTime, dto.getEndTime())
                .orderByDesc(SysLoginInfo::getLoginTime);

        Page<SysLoginInfo> page = loginInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<LoginInfoVO> voList = page.getRecords().stream()
                .map(this::convertToLoginInfoVO)
                .collect(Collectors.toList());

        return R.tableData(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public R<Void> deleteLoginInfos(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return R.ok();
        }
        for (Long id : ids) {
            loginInfoMapper.deleteById(id);
        }
        log.info("[批量删除登录日志] ids={}", ids);
        return R.ok();
    }

    @Override
    public R<Void> clear() {
        try (Connection conn = dataSource.getConnection()) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.runScript(new java.io.StringReader("TRUNCATE TABLE sys_login_info"));
            log.info("[清空登录日志]");
        } catch (Exception e) {
            log.error("[清空登录日志失败] error={}", e.getMessage(), e);
            return R.fail("清空失败");
        }
        return R.ok();
    }

    // ==================== 私有辅助方法 ====================

    private LoginInfoVO convertToLoginInfoVO(SysLoginInfo info) {
        LoginInfoVO vo = new LoginInfoVO();
        vo.setLoginInfoId(info.getLoginInfoId());
        vo.setUserId(info.getUserId());
        vo.setAccount(info.getAccount());
        vo.setIpAddress(info.getIpAddress());
        vo.setLoginLocation(info.getLoginLocation());
        vo.setBrowser(info.getBrowser());
        vo.setOs(info.getOs());
        vo.setStatus(info.getStatus());
        vo.setMsg(info.getMsg());
        vo.setLoginTime(info.getLoginTime());
        return vo;
    }
}
