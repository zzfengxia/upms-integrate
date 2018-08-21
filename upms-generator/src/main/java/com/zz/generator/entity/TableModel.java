package com.zz.generator.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zz.generator.entity.ColumnModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-09 15:57
 * @desc 表信息实体类
 * ************************************
 */
@Data
public class TableModel {
    // 表名
    private String tableName;
    // 表备注
    private String tableComment;
    // engine
    private String engine;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createTime;
    // 主键
    private ColumnModel pk;
    // 列(不包含主键)
    private List<ColumnModel> columns;
}
