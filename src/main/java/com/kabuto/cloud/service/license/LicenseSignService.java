package com.kabuto.cloud.service.license;

/**
 * ECDSA 签名服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LicenseSignService 实现授权签名服务</p>
 * <p><b>解决方案：</b>使用 Java 原生 KeyPairGenerator 实现 ECDSA P-256 签名，
 * 确保与 NestJS 版本签名格式兼容</p>
 * <p><b>原因说明：</b>对应 nest-admin LicenseSignService。客户端需要用公钥验证授权签名的有效性</p>
 */
public interface LicenseSignService {

    /**
     * 获取 ECDSA 公钥（PEM 格式）
     */
    String getPublicKey();

    /**
     * 对数据签名
     *
     * @param data 待签名的 UTF-8 字符串
     * @return Base64 编码的签名值
     */
    String signData(String data);

    /**
     * 对授权数据签名（字段按字母排序拼接后签名，确保确定性）
     *
     * @param licenseCode      授权码
     * @param deviceFingerprint 设备指纹
     * @param activatedAt       激活时间（ISO-8601）
     * @param expiresAt         过期时间（ISO-8601，可为 null）
     * @param planType          计划类型
     * @return Base64 编码的签名值
     */
    String signLicenseData(String licenseCode, String deviceFingerprint,
                           String activatedAt, String expiresAt, String planType);
}
