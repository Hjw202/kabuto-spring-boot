package com.kabuto.cloud.service.impl.system;

import com.kabuto.cloud.service.system.LicenseSignService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;

/**
 * ECDSA P-256 签名服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LicenseSignService 实现授权签名服务</p>
 * <p><b>解决方案：</b>使用 Java 原生 KeyPairGenerator 实现 ECDSA P-256 签名，
 * SHA-256 摘要 + IEEE-P1363 编码格式。密钥对持久化到文件，首次启动自动生成</p>
 * <p><b>原因说明：</b>对应 nest-admin LicenseSignService 的 Node.js crypto.generateKeyPairSync 实现。
 * 使用相同的 ECDSA P-256 算法和签名字段排序规则，确保签名格式兼容</p>
 */
@Slf4j
@Service
public class LicenseSignServiceImpl implements LicenseSignService {

    @Value("${license.keys-path:./keys}")
    private String keysPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String publicKeyPem;

    @PostConstruct
    public void init() {
        loadOrGenerateKeys();
    }

    @Override
    public String getPublicKey() {
        return publicKeyPem;
    }

    @Override
    public String signData(String data) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] sigBytes = signature.sign();

            // 转为 IEEE-P1363 格式（与 NestJS 版本兼容）
            byte[] ieeeFormat = convertDERToIEEE(sigBytes, 32);

            return Base64.getEncoder().encodeToString(ieeeFormat);
        } catch (Exception e) {
            log.error("[签名失败] error={}", e.getMessage(), e);
            throw new RuntimeException("签名失败", e);
        }
    }

    @Override
    public String signLicenseData(String licenseCode, String deviceFingerprint,
                                  String activatedAt, String expiresAt, String planType) {
        // 字段按字母排序拼接，确保确定性签名（与 NestJS 版本一致）
        StringBuilder sb = new StringBuilder();
        sb.append("activatedAt=").append(activatedAt != null ? activatedAt : "").append("&");
        sb.append("deviceFingerprint=").append(deviceFingerprint != null ? deviceFingerprint : "").append("&");
        sb.append("expiresAt=").append(expiresAt != null ? expiresAt : "").append("&");
        sb.append("licenseCode=").append(licenseCode != null ? licenseCode : "").append("&");
        sb.append("planType=").append(planType != null ? planType : "");

        return signData(sb.toString());
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 加载或生成密钥对
     */
    private void loadOrGenerateKeys() {
        Path dir = Paths.get(keysPath);
        Path privateKeyPath = dir.resolve("license_private.pem");
        Path publicKeyPath = dir.resolve("license_public.pem");

        // 尝试从文件加载
        if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
            try {
                String privatePem = Files.readString(privateKeyPath, StandardCharsets.UTF_8);
                String publicPem = Files.readString(publicKeyPath, StandardCharsets.UTF_8);

                KeyFactory kf = KeyFactory.getInstance("EC");

                // 解析私钥
                String privateBase64 = privatePem
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");
                this.privateKey = kf.generatePrivate(
                        new java.security.spec.PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateBase64)));

                // 解析公钥
                String publicBase64 = publicPem
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");
                this.publicKey = kf.generatePublic(
                        new java.security.spec.X509EncodedKeySpec(Base64.getDecoder().decode(publicBase64)));
                this.publicKeyPem = publicPem;

                log.info("[License签名] 从文件加载密钥对成功, path={}", dir.toAbsolutePath());
                return;
            } catch (Exception e) {
                log.warn("[License签名] 加载密钥对失败，将重新生成: {}", e.getMessage());
            }
        }

        // 生成新密钥对
        generateKeys(dir, privateKeyPath, publicKeyPath);
    }

    /**
     * 生成 ECDSA P-256 密钥对
     */
    private void generateKeys(Path dir, Path privateKeyPath, Path publicKeyPath) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
            KeyPair keyPair = kpg.generateKeyPair();

            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

            String privatePem = "-----BEGIN PRIVATE KEY-----\n" +
                    Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(privateKey.getEncoded()) +
                    "\n-----END PRIVATE KEY-----\n";
            String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(publicKey.getEncoded()) +
                    "\n-----END PUBLIC KEY-----\n";
            this.publicKeyPem = publicPem;

            // 持久化到文件
            try {
                Files.createDirectories(dir);
                Files.writeString(privateKeyPath, privatePem, StandardCharsets.UTF_8);
                Files.writeString(publicKeyPath, publicPem, StandardCharsets.UTF_8);
                log.info("[License签名] 密钥对已生成并保存到 {}", dir.toAbsolutePath());
            } catch (IOException e) {
                log.warn("[License签名] 密钥对已生成但保存失败（内存模式）: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("[License签名] 密钥对生成失败", e);
            throw new RuntimeException("ECDSA 密钥对生成失败", e);
        }
    }

    /**
     * DER 编码的 ECDSA 签名转 IEEE-P1363 格式
     * DER: SEQUENCE { INTEGER r, INTEGER s }
     * IEEE-P1363: r || s（固定长度，左补零）
     */
    private byte[] convertDERToIEEE(byte[] derSignature, int componentSize) throws Exception {
        // 简化解析：直接使用 Signature 签名后通过 ASN.1 解析
        // DER 格式: 0x30 len 0x02 rLen r... 0x02 sLen s...
        int offset = 2; // 跳过 SEQUENCE tag + length

        // 解析 r
        if (derSignature[offset] != 0x02) throw new SignatureException("Invalid DER: expected INTEGER for r");
        offset++;
        int rLen = derSignature[offset] & 0xFF;
        offset++;
        byte[] r = new byte[rLen];
        System.arraycopy(derSignature, offset, r, 0, rLen);
        offset += rLen;

        // 解析 s
        if (derSignature[offset] != 0x02) throw new SignatureException("Invalid DER: expected INTEGER for s");
        offset++;
        int sLen = derSignature[offset] & 0xFF;
        offset++;
        byte[] s = new byte[sLen];
        System.arraycopy(derSignature, offset, s, 0, sLen);

        // 去掉前导零
        r = stripLeadingZeros(r);
        s = stripLeadingZeros(s);

        // 左补零到 componentSize
        byte[] result = new byte[componentSize * 2];
        System.arraycopy(r, 0, result, componentSize - r.length, r.length);
        System.arraycopy(s, 0, result, componentSize * 2 - s.length, s.length);

        return result;
    }

    private byte[] stripLeadingZeros(byte[] input) {
        int i = 0;
        while (i < input.length - 1 && input[i] == 0) i++;
        if (i == 0) return input;
        byte[] result = new byte[input.length - i];
        System.arraycopy(input, i, result, 0, result.length);
        return result;
    }
}
