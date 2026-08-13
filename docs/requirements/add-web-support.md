# 需求记录：添加 Web 支持（解决启动后立即退出问题）

## 需求描述
大王发现应用启动后进程立即退出（退出代码 0），期望应用能持续监听端口运行。

## 问题分析
项目中仅依赖 `spring-boot-starter`，它只提供 Spring 核心功能（IoC、AOP、配置管理等），**不包含嵌入式 Web 服务器**。因此应用启动后主线程完成初始化后没有可阻塞的服务，进程自然退出。

## 解决方案
在 `pom.xml` 中添加 `spring-boot-starter-web` 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 原因说明
- `spring-boot-starter-web` 是 Spring Boot 的标准 Web 开发启动器
- 它会自动引入嵌入式 Tomcat、Spring MVC、Jackson 等组件
- Tomcat 启动后会持续监听 HTTP 端口（默认 8080），主线程阻塞等待请求，进程不会退出
- 这是 Spring Boot Web 应用的标准做法，符合 Spring Boot 的约定优于配置原则

## 后续步骤
添加依赖后，需要重新加载 Maven 依赖（IDE 通常会自动提示），然后重新启动应用即可看到 Tomcat 正常监听端口。
