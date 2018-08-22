package com.zz.generator.entity;

import lombok.Data;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-09 15:54
 * @desc 字段信息实体类
 * ************************************
 */
@Data
public class ColumnModel {
    // 字段名
    private String columnName;
    // 数据类型
    private String dataType;
    // 是否非空(YES:NO)
    private String isNullable;
    // 相关健
    private String columnKey;
    // 额外信息
    private String extra;
    // 字段备注
    private String columnComment;

    public ColumnModel() {
    }
}
