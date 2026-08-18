package com.kabuto.cloud.service.impl.license;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dao.license.SysLicenseDeviceMapper;
import com.kabuto.cloud.dao.license.SysLicenseMapper;
import com.kabuto.cloud.dto.license.*;
import com.kabuto.cloud.entity.license.SysLicense;
import com.kabuto.cloud.entity.license.SysLicenseDevice;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.license.LicenseSignService;
import com.kabuto.cloud.service.license.SysLicenseService;
import com.kabuto.cloud.vo.license.LicenseActivateVO;
import com.kabuto.cloud.vo.license.LicenseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * License 授权码服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LicenseService 实现 License 授权业务逻辑</p>
 * <p><b>解决方案：</b>实现授权码激活、验证、解绑、设备绑定管理等核心功能，
 * 配合 LicenseSignService 完成 ECDSA 签名校验</p>
 * <p><b>原因说明：</b>对应 nest-admin LicenseService。授权码格式支持 DORA- 前缀，
 * 设备绑定通过 deviceFingerprint 实现，支持多设备上限控制</p>
 */
@Slf4j
@Service
public class SysLicenseServiceImpl implements SysLicenseService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final SysLicenseMapper licenseMapper;
    private final SysLicenseDeviceMapper licenseDeviceMapper;
    private final LicenseSignService licenseSignService;

    public SysLicenseServiceImpl(SysLicenseMapper licenseMapper,
                                 SysLicenseDeviceMapper licenseDeviceMapper,
                                 LicenseSignService licenseSignService) {
        this.licenseMapper = licenseMapper;
        this.licenseDeviceMapper = licenseDeviceMapper;
        this.licenseSignService = licenseSignService;
    }

    // ==================== 客户端接口 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LicenseActivateVO activate(ActivateLicenseDTO dto) {
        String code = normalizeLicenseCode(dto.getLicenseCode());

        // 查找授权码
        SysLicense license = licenseMapper.selectOne(
                new LambdaQueryWrapper<SysLicense>().eq(SysLicense::getLicenseCode, code));
        if (license == null) {
            throw new BizException(ResultCode.NOT_FOUND, "授权码不存在");
        }

        // 校验状态
        if (Integer.valueOf(3).equals(license.getStatus())) {
            throw new BizException(ResultCode.BAD_REQUEST, "授权码已被禁用");
        }
        if (Integer.valueOf(2).equals(license.getStatus())) {
            throw new BizException(ResultCode.BAD_REQUEST, "授权码已过期");
        }

        // 校验过期时间
        if (license.getExpiresAt() != null && license.getExpiresAt().isBefore(LocalDateTime.now())) {
            license.setStatus(2); // 已过期
            licenseMapper.updateById(license);
            throw new BizException(ResultCode.BAD_REQUEST, "授权码已过期");
        }

        // 检查设备是否已绑定
        SysLicenseDevice existDevice = licenseDeviceMapper.selectOne(
                new LambdaQueryWrapper<SysLicenseDevice>()
                        .eq(SysLicenseDevice::getLicenseId, license.getLicenseId())
                        .eq(SysLicenseDevice::getDeviceFingerprint, dto.getDeviceFingerprint())
                        .eq(SysLicenseDevice::getIsActive, 1));

        if (existDevice != null) {
            // 已绑定，更新最后验证时间，返回新签名
            existDevice.setLastValidated(LocalDateTime.now());
            licenseDeviceMapper.updateById(existDevice);
            return buildActivateResponse(license, dto.getDeviceFingerprint());
        }

        // 检查设备数上限
        Long deviceCount = licenseDeviceMapper.selectCount(
                new LambdaQueryWrapper<SysLicenseDevice>()
                        .eq(SysLicenseDevice::getLicenseId, license.getLicenseId())
                        .eq(SysLicenseDevice::getIsActive, 1));
        if (deviceCount >= (license.getMaxDevices() != null ? license.getMaxDevices() : 1)) {
            throw new BizException(ResultCode.BAD_REQUEST, "设备数已达上限（" + license.getMaxDevices() + "）");
        }

        // 创建设备绑定
        SysLicenseDevice device = new SysLicenseDevice();
        device.setLicenseId(license.getLicenseId());
        device.setDeviceFingerprint(dto.getDeviceFingerprint());
        device.setDeviceName(dto.getDeviceName());
        device.setActivatedAt(LocalDateTime.now());
        device.setLastValidated(LocalDateTime.now());
        device.setIsActive(1);
        device.setCreateBy("system");
        device.setCreateTime(LocalDateTime.now());
        licenseDeviceMapper.insert(device);

        // 更新授权码状态
        if (Integer.valueOf(0).equals(license.getStatus())) {
            license.setStatus(1); // 从未使用变为已激活
            license.setActivatedAt(LocalDateTime.now());
            licenseMapper.updateById(license);
        }

        return buildActivateResponse(license, dto.getDeviceFingerprint());
    }

    @Override
    public ValidateResult validate(ValidateLicenseDTO dto) {
        String code = normalizeLicenseCode(dto.getLicenseCode());
        ValidateResult result = new ValidateResult();

        // 查找授权码
        SysLicense license = licenseMapper.selectOne(
                new LambdaQueryWrapper<SysLicense>().eq(SysLicense::getLicenseCode, code));
        if (license == null) {
            result.setValid(false);
            result.setError("LICENSE_NOT_FOUND");
            return result;
        }

        // 校验禁用
        if (Integer.valueOf(3).equals(license.getStatus())) {
            result.setValid(false);
            result.setError("LICENSE_DISABLED");
            return result;
        }

        // 校验过期
        if (license.getExpiresAt() != null && license.getExpiresAt().isBefore(LocalDateTime.now())) {
            result.setValid(false);
            result.setError("LICENSE_EXPIRED");
            return result;
        }

        // 查找设备绑定
        SysLicenseDevice device = licenseDeviceMapper.selectOne(
                new LambdaQueryWrapper<SysLicenseDevice>()
                        .eq(SysLicenseDevice::getLicenseId, license.getLicenseId())
                        .eq(SysLicenseDevice::getDeviceFingerprint, dto.getDeviceFingerprint())
                        .eq(SysLicenseDevice::getIsActive, 1));

        if (device == null) {
            result.setValid(false);
            result.setError("DEVICE_NOT_ACTIVATED");
            return result;
        }

        // 更新最后验证时间
        device.setLastValidated(LocalDateTime.now());
        licenseDeviceMapper.updateById(device);

        // 返回有效结果 + 签名信息
        result.setValid(true);
        LicenseActivateVO data = new LicenseActivateVO();
        data.setActivatedAt(device.getActivatedAt());
        data.setExpiresAt(license.getExpiresAt());
        data.setPlanType(license.getPlanType());
        data.setSignature(buildSignature(license, dto.getDeviceFingerprint(),
                device.getActivatedAt()));
        result.setData(data);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deactivate(String licenseCode, String deviceFingerprint) {
        String code = normalizeLicenseCode(licenseCode);

        SysLicense license = licenseMapper.selectOne(
                new LambdaQueryWrapper<SysLicense>().eq(SysLicense::getLicenseCode, code));
        if (license == null) {
            return false;
        }

        // 找到活跃的设备绑定并停用
        SysLicenseDevice device = licenseDeviceMapper.selectOne(
                new LambdaQueryWrapper<SysLicenseDevice>()
                        .eq(SysLicenseDevice::getLicenseId, license.getLicenseId())
                        .eq(SysLicenseDevice::getDeviceFingerprint, deviceFingerprint)
                        .eq(SysLicenseDevice::getIsActive, 1));

        if (device == null) {
            return false;
        }

        device.setIsActive(0);
        device.setUpdateTime(LocalDateTime.now());
        licenseDeviceMapper.updateById(device);

        log.info("[解绑设备] licenseId={}, fingerprint={}", license.getLicenseId(), deviceFingerprint);
        return true;
    }

    @Override
    public String getPublicKey() {
        return licenseSignService.getPublicKey();
    }

    // ==================== 管理端接口 ====================

    @Override
    public PageResult<LicenseVO> page(Integer pageNum, Integer pageSize, SearchLicenseDTO dto) {
        LambdaQueryWrapper<SysLicense> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getStatus() != null, SysLicense::getStatus, dto.getStatus())
                .eq(StringUtils.hasText(dto.getPlanType()), SysLicense::getPlanType, dto.getPlanType())
                .orderByDesc(SysLicense::getCreateTime);

        Page<SysLicense> page = licenseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<LicenseVO> voList = page.getRecords().stream()
                .map(this::convertToLicenseVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public LicenseVO getLicenseDetail(Long licenseId) {
        SysLicense license = licenseMapper.selectById(licenseId);
        if (license == null) {
            throw new BizException(ResultCode.NOT_FOUND, "授权码不存在");
        }
        return convertToLicenseVO(license);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LicenseVO createLicense(CreateLicenseDTO dto) {
        SysLicense license = new SysLicense();
        license.setLicenseCode(generateLicenseCode());
        license.setPlanType(dto.getPlanType());
        license.setMaxDevices(dto.getMaxDevices() != null ? dto.getMaxDevices() : 1);
        license.setExpiresAt(dto.getExpiresAt());
        license.setNote(dto.getNote());
        license.setStatus(0); // 未使用
        license.setCreateBy("admin");
        license.setCreateTime(LocalDateTime.now());
        licenseMapper.insert(license);

        log.info("[创建授权码] licenseId={}, code={}, planType={}",
                license.getLicenseId(), license.getLicenseCode(), license.getPlanType());
        return convertToLicenseVO(license);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 标准化授权码：去掉 DORA- 前缀，去除非 hex 字符，转大写
     */
    private String normalizeLicenseCode(String code) {
        if (!StringUtils.hasText(code)) return code;
        String normalized = code;
        if (normalized.toUpperCase().startsWith("DORA-")) {
            normalized = normalized.substring(5);
        }
        return normalized.replaceAll("[^0-9a-fA-F]", "").toUpperCase();
    }

    /**
     * 生成 16 字符大写 hex 授权码
     */
    private String generateLicenseCode() {
        byte[] bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 构建激活响应
     */
    private LicenseActivateVO buildActivateResponse(SysLicense license, String deviceFingerprint) {
        LocalDateTime activatedAt = license.getActivatedAt() != null ? license.getActivatedAt() : LocalDateTime.now();
        String signature = buildSignature(license, deviceFingerprint, activatedAt);

        LicenseActivateVO vo = new LicenseActivateVO();
        vo.setActivatedAt(activatedAt);
        vo.setExpiresAt(license.getExpiresAt());
        vo.setPlanType(license.getPlanType());
        vo.setSignature(signature);

        // 保存签名到授权码
        license.setSignature(signature);
        licenseMapper.updateById(license);

        return vo;
    }

    /**
     * 构建 ECDSA 签名
     */
    private String buildSignature(SysLicense license, String deviceFingerprint, LocalDateTime activatedAt) {
        return licenseSignService.signLicenseData(
                license.getLicenseCode(),
                deviceFingerprint,
                activatedAt != null ? activatedAt.format(ISO_FMT) : null,
                license.getExpiresAt() != null ? license.getExpiresAt().format(ISO_FMT) : null,
                license.getPlanType());
    }

    /**
     * 转换实体为 VO（排除 signature）
     */
    private LicenseVO convertToLicenseVO(SysLicense license) {
        LicenseVO vo = new LicenseVO();
        vo.setLicenseId(license.getLicenseId());
        vo.setLicenseCode(license.getLicenseCode());
        vo.setPlanType(license.getPlanType());
        vo.setMaxDevices(license.getMaxDevices());
        vo.setStatus(license.getStatus());
        vo.setActivatedAt(license.getActivatedAt());
        vo.setExpiresAt(license.getExpiresAt());
        vo.setNote(license.getNote());
        vo.setCreateBy(license.getCreateBy());
        vo.setCreateTime(license.getCreateTime());
        return vo;
    }
}
