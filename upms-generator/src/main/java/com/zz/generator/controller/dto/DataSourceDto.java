package com.zz.generator.controller.dto;

import lombok.Data;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-13 10:05
 * @desc DataSourceDto
 * ************************************
 */
@Data
public class DataSourceDto {
    // 驱动名
    private String driverName = "com.mysql.jdbc.Driver";
    // 数据源url
    private String dataSourceUrl;
    // 数据源用户名
    private String username;
    // 数据源密码
    private String password;
}
