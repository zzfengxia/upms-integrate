package com.zz.generator.controller.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-10 11:16
 * @desc 全局配置
 * ************************************
 */
@Getter
@Setter
public class GeneratorConfig {
    // 数据源配置
    private DataSourceDto dataSource;
    // 包名
    private String packageName;
    // 模块名
    private String moduleName;
    // 作者名
    private String author;
    // email
    private String email;
    // 是否使用lombok
    private boolean enableLombok = true;
    // 命名转换，默认驼峰式命名
    private String namingConversion = "camelCase";
    // 是否移除表名前缀
    private boolean removePrefix = true;
    // 文件保存目录
    private String outputPath;
}
