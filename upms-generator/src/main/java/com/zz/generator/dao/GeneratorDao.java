package com.zz.generator.dao;

import com.zz.generator.common.PageParam;
import com.zz.generator.entity.ColumnModel;
import com.zz.generator.entity.TableModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-13 16:43
 * @desc GeneratorDao
 * ************************************
 */
@Mapper
public interface GeneratorDao {
    List<TableModel> queryTableList(PageParam params);

    int tableCount(PageParam params);

    TableModel queryTable(String tableName);

    List<ColumnModel> queryColumns(String tableName);
}
