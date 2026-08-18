package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.*;
import com.kabuto.cloud.vo.system.LicenseActivateVO;
import com.kabuto.cloud.vo.system.LicenseVO;

/**
 * License 授权码服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LicenseService 实现 License 服务层</p>
 * <p><b>解决方案：</b>定义客户端接口（激活/验证/解绑/公钥）与管理端接口（创建/详情/列表）</p>
 * <p><b>原因说明：</b>对应 nest-admin LicenseService，支持设备绑定与 ECDSA 签名校验</p>
 */
public interface SysLicenseService {

    // 客户端接口（通常 @Public）
    R<LicenseActivateVO> activate(ActivateLicenseDTO dto);
    R<ValidateResult> validate(ValidateLicenseDTO dto);
    R<Boolean> deactivate(String licenseCode, String deviceFingerprint);
    String getPublicKey();

    // 管理端接口
    R<PageResult<LicenseVO>> page(Integer pageNum, Integer pageSize, SearchLicenseDTO dto);
    R<LicenseVO> getLicenseDetail(Long licenseId);
    R<LicenseVO> createLicense(CreateLicenseDTO dto);

    /**
     * 验证结果（简单封装）
     */
    @lombok.Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "授权验证结果")
    class ValidateResult {
        @io.swagger.v3.oas.annotations.media.Schema(description = "是否有效")
        private boolean valid;
        @io.swagger.v3.oas.annotations.media.Schema(description = "错误码/信息（可选）")
        private String error;
        @io.swagger.v3.oas.annotations.media.Schema(description = "激活信息（valid=true时返回）")
        private LicenseActivateVO data;
    }
}
