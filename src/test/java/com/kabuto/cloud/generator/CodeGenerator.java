package com.kabuto.cloud.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * MyBatis Plus 代码生成器
 * <p>
 * 使用方法：
 * 1. 确保数据库中已创建目标表
 * 2. 修改本类中的数据库连接信息和表名
 * 3. 在 IDE 中右键运行 CodeGenerator.main()
 */
public class CodeGenerator {

    // ========== 数据库连接配置（请根据实际情况修改）==========
    private static final String URL = "jdbc:mysql://localhost:3306/kabuto_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "your_password";

    // 作者名（生成在类注释中）
    private static final String AUTHOR = "kabuto";

    // 父包名
    private static final String PARENT_PACKAGE = "com.kabuto.cloud";

    // 要生成代码的表名（多个表用逗号分隔，如 {"user", "order", "product"}）
    private static final String[] TABLES = {"user"};

    public static void main(String[] args) {
        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author(AUTHOR)
                            // 输出目录（项目根目录下的 src/main/java）
                            .outputDir(System.getProperty("user.dir") + "/src/main/java")
                            // 注释日期格式
                            .commentDate("yyyy-MM-dd");
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(PARENT_PACKAGE)
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .xml("mapper")
                            .controller("controller")
                            // XML 文件输出路径
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    System.getProperty("user.dir") + "/src/main/resources/mapper"
                            ));
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(TABLES)
                            // 实体策略
                            .entityBuilder()
                            // 开启 Lombok
                            .enableLombok()
                            // 开启链式模型
                            .enableChainModel()
                            // 逻辑删除字段
                            .logicDeleteColumnName("deleted")
                            // 添加表字段填充
                            .addTableFills(
                                    new com.baomidou.mybatisplus.generator.fill.Column("create_time", com.baomidou.mybatisplus.annotation.FieldFill.INSERT),
                                    new com.baomidou.mybatisplus.generator.fill.Column("update_time", com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
                            )
                            // 下划线转驼峰
                            .naming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel)
                            .columnNaming(com.baomidou.mybatisplus.generator.config.rules.NamingStrategy.underline_to_camel)
                            // Controller 策略
                            .controllerBuilder()
                            .enableRestStyle()
                            // Service 策略
                            .serviceBuilder()
                            // Mapper 策略
                            .mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList();
                })
                // 使用 Freemarker 模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

        System.out.println("代码生成完成！");
    }
}
