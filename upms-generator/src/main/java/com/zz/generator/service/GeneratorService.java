package com.zz.generator.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.zz.generator.common.Constants;
import com.zz.generator.common.GlobalConfig;
import com.zz.generator.common.PageParam;
import com.zz.generator.common.PageResponse;
import com.zz.generator.config.DynamicDataSource;
import com.zz.generator.controller.dto.DataSourceDto;
import com.zz.generator.dao.GeneratorDao;
import com.zz.generator.entity.ColumnModel;
import com.zz.generator.entity.TableModel;
import com.zz.generator.utils.GeneratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-13 09:52
 * @desc GeneratorService
 * ************************************
 */
@Service
public class GeneratorService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DynamicDataSource dynamicDataSource;
    @Autowired
    private GeneratorDao generatorDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void registryDataSource(DataSourceDto config) {
        Map<String, String> druidConfig = new HashMap<>();
        druidConfig.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, config.getDriverName());
        druidConfig.put(DruidDataSourceFactory.PROP_URL, config.getDataSourceUrl());
        druidConfig.put(DruidDataSourceFactory.PROP_USERNAME, config.getUsername());
        druidConfig.put(DruidDataSourceFactory.PROP_PASSWORD, config.getPassword());

        DruidDataSource druidDataSource = null;
        try {
            druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(druidConfig);
        } catch (Exception e) {
            logger.error("创建数据库连接失败", e);
            throw new IllegalArgumentException("连接失败,参数错误");
        }
        // 设置失败重连次数
        druidDataSource.setConnectionErrorRetryAttempts(2);
        // 取消失败后无限重连
        druidDataSource.setBreakAfterAcquireFailure(true);

        dynamicDataSource.addDataSourceToTargetDataSource(Constants.CUSTOMER_SOURCE_KEY, druidDataSource);
    }

    public boolean connectCheck() {
        // 测试连接
        List res = jdbcTemplate.queryForList("SELECT 1");
        return res.size() > 0;
    }

    public PageResponse queryTablePage(PageParam params) {
        List<TableModel> rows = generatorDao.queryTableList(params);
        int count = generatorDao.tableCount(params);

        return new PageResponse(rows, count);
    }

    public byte[] generateCode(String[] tables) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for(String tableName : tables){
            //查询表信息
            TableModel table = generatorDao.queryTable(tableName);
            //查询列信息
            List<ColumnModel> columns = generatorDao.queryColumns(tableName);
            //生成代码
            GeneratorUtils.gen(table, columns, GlobalConfig.get(), zip);
        }

        try {
            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
