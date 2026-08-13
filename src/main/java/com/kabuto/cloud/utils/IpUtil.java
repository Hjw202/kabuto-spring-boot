package com.kabuto.cloud.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 地址工具类
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 的 parseIp 实现 IP 解析功能</p>
 * <p><b>解决方案：</b>提取客户端真实 IP（支持反向代理），提供 IP 地址解析</p>
 * <p><b>原因说明：</b>对应 nest-admin utils 中的 parseIp 函数。通过 X-Forwarded-For 等 Header
 * 获取真实 IP，解决反向代理场景下的 IP 获取问题</p>
 */
public class IpUtil {

    private IpUtil() {
        // 禁止实例化
    }

    /**
     * 获取客户端真实 IP 地址
     *
     * @param request HttpServletRequest
     * @return 真实 IP
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取 IP 地址（重载，接受字符串 IP）
     */
    public static String getIpAddress(String ip) {
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }

    /**
     * 根据 IP 获取地理位置（简化版，实际可接入 IP 库）
     */
    public static String getIpLocation(String ip) {
        if (ip == null) {
            return "未知";
        }
        // 内网 IP
        if (ip.startsWith("127.") || ip.startsWith("192.168.") ||
                ip.startsWith("10.") || ip.startsWith("172.")) {
            return "内网IP";
        }
        // 实际项目中可接入 IP 定位服务（如 阿里云、百度地图 IP 定位 API）
        return "未知";
    }
}
