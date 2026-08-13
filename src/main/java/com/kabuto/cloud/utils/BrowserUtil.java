package com.kabuto.cloud.utils;

import lombok.Data;

/**
 * 浏览器信息解析工具类
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 的 parseBrowser 实现 User-Agent 解析</p>
 * <p><b>解决方案：</b>解析 User-Agent 字符串，提取操作系统和浏览器类型</p>
 * <p><b>原因说明：</b>对应 nest-admin utils 中的 parseBrowser 函数。用于登录日志记录用户设备信息</p>
 */
public class BrowserUtil {

    private BrowserUtil() {
        // 禁止实例化
    }

    /**
     * 解析 User-Agent
     *
     * @param userAgent User-Agent 字符串
     * @return 浏览器信息
     */
    public static BrowserInfo parse(String userAgent) {
        BrowserInfo info = new BrowserInfo();
        if (userAgent == null || userAgent.isEmpty()) {
            info.setOs("Unknown");
            info.setBrowser("Unknown");
            return info;
        }

        // 解析操作系统
        if (userAgent.contains("Windows")) {
            info.setOs("Windows");
        } else if (userAgent.contains("Mac OS X") || userAgent.contains("Macintosh")) {
            info.setOs("Mac OS");
        } else if (userAgent.contains("Linux")) {
            info.setOs("Linux");
        } else if (userAgent.contains("Android")) {
            info.setOs("Android");
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            info.setOs("iOS");
        } else {
            info.setOs("Unknown");
        }

        // 解析浏览器
        if (userAgent.contains("Edg/")) {
            info.setBrowser("Edge");
        } else if (userAgent.contains("Chrome/") && !userAgent.contains("Edg/")) {
            info.setBrowser("Chrome");
        } else if (userAgent.contains("Firefox/")) {
            info.setBrowser("Firefox");
        } else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) {
            info.setBrowser("Safari");
        } else if (userAgent.contains("Opera/") || userAgent.contains("OPR/")) {
            info.setBrowser("Opera");
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident/")) {
            info.setBrowser("IE");
        } else {
            info.setBrowser("Unknown");
        }

        return info;
    }

    /**
     * 浏览器信息
     */
    @Data
    public static class BrowserInfo {
        /** 操作系统 */
        private String os;
        /** 浏览器类型 */
        private String browser;
    }
}
