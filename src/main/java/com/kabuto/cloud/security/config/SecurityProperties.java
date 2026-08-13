package com.kabuto.cloud.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 安全配置属性
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin JWT 配置实现 Java 版本</p>
 * <p><b>解决方案：</b>使用 @ConfigurationProperties 将 application.yml 中的 jwt 配置绑定到 Java 对象</p>
 * <p><b>原因说明：</b>类型安全的配置注入，避免在代码中硬编码配置项，便于集中管理和动态调整</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class SecurityProperties {

    /** JWT 密钥 */
    private String secret = "kabuto-spring-boot-jwt-secret-key";

    /** Token 过期时间（毫秒） */
    private long expires = 7200000L;

    /** Token 请求头名称 */
    private String header = "Authorization";

    /** Token 前缀 */
    private String prefix = "Bearer ";
}
