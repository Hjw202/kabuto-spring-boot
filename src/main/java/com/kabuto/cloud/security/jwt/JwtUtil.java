package com.kabuto.cloud.security.jwt;

import com.kabuto.cloud.security.config.SecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 的 JwtService 实现 JWT Token 生成与验证</p>
 * <p><b>解决方案：</b>基于 jjwt 0.12.6 实现 JWT 的生成、解析、验证功能</p>
 * <p><b>原因说明：</b>jjwt 0.12.6 支持 Spring Boot 3.x 和 Jakarta EE，API 清晰且安全。
 * 对应 nest-admin 中 jwtService.sign() 和 jwtService.verify() 功能</p>
 */
@Slf4j
@Component
public class JwtUtil {

    private final SecurityProperties securityProperties;
    private final SecretKey secretKey;

    public JwtUtil(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        // 使用 Base64 编码的密钥或原始字符串生成 HMAC-SHA 密钥
        this.secretKey = Keys.hmacShaKeyFor(securityProperties.getSecret().getBytes());
    }

    /**
     * 生成 JWT Token
     *
     * @param claims 自定义声明（payload）
     * @return JWT 字符串
     */
    public String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + securityProperties.getExpires());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析 JWT Token
     *
     * @param token JWT 字符串
     * @return Claims 对象
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT 字符串
     * @return true=有效，false=无效（过期或签名错误）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[JWT] Token 已过期");
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("[JWT] 不支持的 Token 格式");
            return false;
        } catch (MalformedJwtException e) {
            log.warn("[JWT] Token 格式错误");
            return false;
        } catch (SecurityException e) {
            log.warn("[JWT] Token 签名验证失败");
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("[JWT] Token 为空或非法");
            return false;
        }
    }

    /**
     * 从 Token 中提取声明值
     *
     * @param token JWT 字符串
     * @param key   声明键
     * @return 声明值
     */
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String token, String key) {
        Claims claims = parseToken(token);
        return (T) claims.get(key);
    }

    /**
     * 从 Token 中提取用户ID
     */
    public String getUserId(String token) {
        return getClaim(token, "id");
    }

    /**
     * 从 Token 中提取用户名
     */
    public String getUsername(String token) {
        return getClaim(token, "username");
    }

    /**
     * 从 Token 中提取 tokenId
     */
    public String getTokenId(String token) {
        return getClaim(token, "tokenId");
    }

    /**
     * 判断 Token 是否即将过期（剩余时间小于指定阈值）
     *
     * @param token     JWT 字符串
     * @param threshold 阈值（毫秒）
     * @return true=即将过期
     */
    public boolean isTokenExpired(String token, long threshold) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis() < threshold;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取 Token 过期时间
     */
    public Date getExpirationDate(String token) {
        return parseToken(token).getExpiration();
    }
}
