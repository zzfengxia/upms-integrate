package com.zz.mq.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ************************************
 * create by Intellij IDEA
 * @see com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler
 * @author Francis.zz
 * @date 2022-01-25 10:40
 * ************************************
 */
public class AbstractJSONObjectTypeHandler<T> extends BaseTypeHandler<T> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return null;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
