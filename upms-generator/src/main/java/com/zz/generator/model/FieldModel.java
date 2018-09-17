package com.zz.generator.model;

import com.zz.generator.entity.ColumnModel;
import lombok.Data;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-09 16:12
 * @desc 属性模型
 * ************************************
 */
@Data
public class FieldModel {
    private String columnName;
    private String fieldName;
    private String jdbcType;
    private String fieldType;
    private String fieldDesc;
    // 是否非空 true:非空
    private boolean notNull;
    // 主键标识
    private boolean priFalg;
    // 备注(主键是否自增长等)
    private String extra;

    public FieldModel() {
    }

    public FieldModel(ColumnModel cm) {
        this.columnName = cm.getColumnName();
        this.fieldName = cm.getColumnName();
        this.fieldType = cm.getDataType();
        this.jdbcType = cm.getDataType();
        this.fieldDesc = cm.getColumnComment();
        this.notNull = cm.getIsNullable() != null && !"YES".equalsIgnoreCase(cm.getIsNullable());
        this.priFalg = "PRI".equalsIgnoreCase(cm.getColumnKey());
        this.extra = cm.getExtra();
    }
}
