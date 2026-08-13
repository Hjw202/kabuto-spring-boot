# 需求记录：添加启动端口配置

## 需求描述
大王提出在配置文件中添加 Spring Boot 应用的启动端口配置。

## 解决方案
在 `src/main/resources/application.yml` 中添加 `server.port` 配置项，设置为 `8080`。

```yaml
spring:
  application:
    name: kabuto-spring-boot

server:
  port: 8080
```

## 原因说明
- `server.port` 是 Spring Boot 的标准配置属性，用于指定嵌入式 Web 服务器（Tomcat）的监听端口
- 显式声明端口配置，便于开发团队成员快速了解服务访问地址，也便于后续环境差异化调整
- 端口 `8080` 是 Spring Boot 的默认端口，广泛被开发者熟悉和使用
- 采用 YAML 格式的层级缩进风格，与现有 `spring.application.name` 配置保持一致
