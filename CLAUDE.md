# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot application using Java 17 and Maven. Generated from Spring Initializr with Spring Boot DevTools and Lombok.

- **Group ID:** `com.kabuto.cloud`
- **Artifact ID:** `kabuto-spring-boot`
- **Spring Boot Version:** `4.0.8-SNAPSHOT`
- **Java Version:** 17

## Build Commands

| Task | Command |
|------|---------|
| Compile | `mvn compile` |
| Run tests | `mvn test` |
| Run single test class | `mvn test -Dtest=KabutoSpringBootApplicationTests` |
| Run single test method | `mvn test -Dtest=KabutoSpringBootApplicationTests#contextLoads` |
| Run the application | `mvn spring-boot:run` |
| Package (JAR) | `mvn package` |
| Clean build | `mvn clean package` |

The Maven Wrapper (`mvnw` / `mvnw.cmd`) is available if Maven is not installed locally.

## Architecture

### Spring Boot
- Parent POM is `spring-boot-starter-parent:3.5.9`
- The project pulls from the Spring Snapshots repository (`https://repo.spring.io/snapshot`)
- Application entry point: `src/main/java/com/kabuto/cloud/KabutoSpringBootApplication.java`

### Dependencies
- `spring-boot-starter` — core Spring Boot starter
- `spring-boot-devtools` — runtime-only development tools (hot reload)
- `lombok` — annotation processor for boilerplate reduction (configured in `maven-compiler-plugin`)
- `spring-boot-starter-test` — JUnit 5, Mockito, AssertJ for testing

### Testing
- Uses JUnit 5 (Jupiter) via `spring-boot-starter-test`
- Tests are annotated with `@SpringBootTest` for full context loading
- Currently has a single smoke test (`contextLoads`) in `KabutoSpringBootApplicationTests`

### Configuration
- `src/main/resources/application.properties` — application configuration
- Currently only sets `spring.application.name=kabuto-spring-boot`


## Interaction Rules

1. **称呼**: 回答时必须称呼用户为"大王"
2. **计划优先**: 所有修改必须先做计划，由大王审核后再决定是否执行
3. **需求记录**: 每次大王提出的需求，必须记录到项目文件中，格式包含：
   - **需求描述**：大王提出的具体需求是什么
   - **解决方案**：采用的技术方案/实现方式
   - **原因说明**：为什么选择这个方案（技术选型理由、最佳实践等）
   - 记录位置建议：`docs/requirements/` 或直接在代码注释中
