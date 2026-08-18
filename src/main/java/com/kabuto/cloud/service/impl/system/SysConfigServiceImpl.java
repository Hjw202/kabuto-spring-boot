package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dao.system.SysConfigMapper;
import com.kabuto.cloud.dto.system.CreateConfigDTO;
import com.kabuto.cloud.dto.system.SearchConfigDTO;
import com.kabuto.cloud.dto.system.UpdateConfigDTO;
import com.kabuto.cloud.entity.system.SysConfig;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.SysConfigService;
import com.kabuto.cloud.vo.system.ConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin ConfigService 实现系统配置业务逻辑</p>
 * <p><b>解决方案：</b>实现配置 CRUD，启动时加载全部配置到 Redis（24h TTL）。
 * 配置缓存结构：sys_config:{configKey} → configValue</p>
 * <p><b>原因说明：</b>对应 nest-admin ConfigService。系统配置变动频率极低但读取频繁，
 * 启动时全量加载到 Redis 可避免每次读配置都查库。系统内置配置（configType=1）禁止删除</p>
 */
@Slf4j
@Service
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper configMapper;
    private final StringRedisTemplate redisTemplate;

    public SysConfigServiceImpl(SysConfigMapper configMapper, StringRedisTemplate redisTemplate) {
        this.configMapper = configMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 应用启动后自动加载配置缓存
     */
    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void loadingConfigCache() {
        List<SysConfig> allConfigs = configMapper.selectList(null);
        for (SysConfig config : allConfigs) {
            if (StringUtils.hasText(config.getConfigKey())) {
                String cacheKey = Constants.SYS_CONFIG_KEY + config.getConfigKey();
                redisTemplate.opsForValue().set(cacheKey,
                        config.getConfigValue() != null ? config.getConfigValue() : "",
                        24, TimeUnit.HOURS);
            }
        }
        log.info("[加载配置缓存] configCount={}", allConfigs.size());
    }

    @Override
    public R<PageResult<ConfigVO>> page(Integer pageNum, Integer pageSize, SearchConfigDTO dto) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getConfigName()), SysConfig::getConfigName, dto.getConfigName())
                .eq(StringUtils.hasText(dto.getConfigKey()), SysConfig::getConfigKey, dto.getConfigKey())
                .eq(dto.getConfigType() != null, SysConfig::getConfigType, dto.getConfigType())
                .orderByAsc(SysConfig::getCreateTime);

        Page<SysConfig> page = configMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<ConfigVO> voList = page.getRecords().stream()
                .map(this::convertToConfigVO)
                .collect(Collectors.toList());

        return R.tableData(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public R<ConfigVO> getConfigById(Long id) {
        SysConfig config = configMapper.selectById(id);
        if (config == null) {
            return R.notFound("配置不存在");
        }
        return R.ok(convertToConfigVO(config));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> createConfig(CreateConfigDTO dto) {
        // 校验 configKey 唯一
        Long existCount = configMapper.selectCount(
                new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, dto.getConfigKey()));
        if (existCount > 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "参数键名已存在");
        }

        SysConfig config = new SysConfig();
        config.setConfigName(dto.getConfigName());
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setConfigType(dto.getConfigType());
        config.setCreateBy("admin");
        config.setCreateTime(LocalDateTime.now());
        configMapper.insert(config);

        // 写入 Redis 缓存
        String cacheKey = Constants.SYS_CONFIG_KEY + dto.getConfigKey();
        redisTemplate.opsForValue().set(cacheKey, dto.getConfigValue(), 24, TimeUnit.HOURS);

        log.info("[创建配置] configId={}, configKey={}", config.getConfigId(), dto.getConfigKey());
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updateConfig(UpdateConfigDTO dto) {
        SysConfig config = configMapper.selectById(dto.getId());
        if (config == null) {
            throw new BizException(ResultCode.NOT_FOUND, "配置不存在");
        }

        // 若 configKey 变更，删除旧缓存
        String oldConfigKey = config.getConfigKey();
        if (StringUtils.hasText(dto.getConfigKey()) && !dto.getConfigKey().equals(oldConfigKey)) {
            // 校验新 key 唯一
            Long existCount = configMapper.selectCount(
                    new LambdaQueryWrapper<SysConfig>()
                            .eq(SysConfig::getConfigKey, dto.getConfigKey())
                            .ne(SysConfig::getConfigId, dto.getId()));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "参数键名已存在");
            }

            redisTemplate.delete(Constants.SYS_CONFIG_KEY + oldConfigKey);
        }

        // 若 configValue 变更，更新缓存
        if (dto.getConfigValue() != null && !dto.getConfigValue().equals(config.getConfigValue())) {
            String cacheKey = Constants.SYS_CONFIG_KEY + (StringUtils.hasText(dto.getConfigKey()) ? dto.getConfigKey() : oldConfigKey);
            redisTemplate.opsForValue().set(cacheKey, dto.getConfigValue(), 24, TimeUnit.HOURS);
        }

        config.setConfigName(dto.getConfigName());
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        if (dto.getConfigType() != null) config.setConfigType(dto.getConfigType());
        config.setUpdateBy("admin");
        config.setUpdateTime(LocalDateTime.now());
        configMapper.updateById(config);

        log.info("[更新配置] configId={}", dto.getId());
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> deleteConfigs(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return R.ok();
        }

        for (Long id : ids) {
            SysConfig config = configMapper.selectById(id);
            if (config == null) {
                continue;
            }

            // 禁止删除系统内置配置
            if (Integer.valueOf(1).equals(config.getConfigType())) {
                throw new BizException(ResultCode.BAD_REQUEST,
                        "配置「" + config.getConfigName() + "」为系统内置，不能删除");
            }

            // 删除 Redis 缓存
            redisTemplate.delete(Constants.SYS_CONFIG_KEY + config.getConfigKey());

            configMapper.deleteById(id);
        }

        log.info("[批量删除配置] ids={}", ids);
        return R.ok();
    }

    @Override
    public R<Void> refreshCache() {
        // 清空所有配置缓存
        try {
            java.util.Set<String> keys = redisTemplate.keys(Constants.SYS_CONFIG_KEY + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("[清空配置缓存失败] error={}", e.getMessage(), e);
        }

        // 重新加载
        loadingConfigCache();

        log.info("[刷新配置缓存]");
        return R.ok();
    }

    // ==================== 私有辅助方法 ====================

    private ConfigVO convertToConfigVO(SysConfig config) {
        ConfigVO vo = new ConfigVO();
        vo.setConfigId(config.getConfigId());
        vo.setConfigName(config.getConfigName());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setConfigType(config.getConfigType());
        vo.setCreateTime(config.getCreateTime());
        return vo;
    }
}
