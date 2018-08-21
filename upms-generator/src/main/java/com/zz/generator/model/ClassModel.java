package com.zz.generator.model;

import com.zz.generator.entity.TableModel;
import lombok.Data;

import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-09 15:56
 * @desc 类/实体模型
 * ************************************
 */
@Data
public class ClassModel {
    private String className;
    private String tableName;
    // 描述
    private String desc;
    private List<FieldModel> fields;

    public ClassModel() {
    }

    public ClassModel(TableModel tm) {
        this.tableName = tm.getTableName();
        this.className = tm.getTableName();
        this.desc = tm.getTableComment();
    }
}
