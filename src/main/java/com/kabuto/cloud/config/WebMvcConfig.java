package com.kabuto.cloud.config;

import com.kabuto.cloud.security.filter.AuthTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * <p><b>需求描述：</b>大王要求配置认证过滤器和跨域支持</p>
 * <p><b>解决方案：</b>注册 AuthTokenFilter 到 Filter 链，配置 CORS 跨域</p>
 * <p><b>原因说明：</b>AuthTokenFilter 需要注册到 Servlet Filter 链中才能生效。
 * CORS 配置允许前端跨域调用接口</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 注册认证 Token 过滤器
     */
    @Bean
    public FilterRegistrationBean<AuthTokenFilter> authTokenFilterRegistration(AuthTokenFilter authTokenFilter) {
        FilterRegistrationBean<AuthTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(authTokenFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);  // 优先级设为 1，尽早执行
        registration.setName("authTokenFilter");
        return registration;
    }

    /**
     * CORS 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 允许的前端域名（生产环境应配置具体域名）
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // 允许携带 Token
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
