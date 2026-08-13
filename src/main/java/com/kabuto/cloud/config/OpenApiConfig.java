package com.kabuto.cloud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置类
 * <p>
 * 配置 API 文档的元信息，包括标题、版本、描述、联系人等。
 * 这些信息会显示在 Swagger UI 页面和生成的 OpenAPI JSON/YAML 中。
 * </p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        // API 文档标题
                        .title("Kabuto API 文档")
                        // API 文档版本号（跟随项目版本）
                        .version("v1.0.0")
                        // API 文档描述
                        .description("Kabuto Spring Boot 项目的 RESTful API 接口文档\n\n" +
                                "**用途**：供前端、测试、第三方接入使用，支持导入到 Apifox、Postman、Swagger Editor 等工具。\n\n" +
                                "**导入方式**：访问 `/v3/api-docs` 获取 OpenAPI 3.0 JSON，复制内容后导入到目标工具。")
                        // 联系人信息
                        .contact(new Contact()
                                .name("Kabuto Team")
                                .email("dev@kabuto.cloud")
                                .url("https://kabuto.cloud"))
                        // 许可证信息
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
