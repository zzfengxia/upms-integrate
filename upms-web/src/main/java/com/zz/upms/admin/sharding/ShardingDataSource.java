package com.zz.upms.admin.sharding;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlShardingRuleConfiguration;
import org.apache.shardingsphere.core.yaml.constructor.YamlRootShardingConfigurationConstructor;
import org.apache.shardingsphere.core.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.jdbc.adapter.AbstractDataSourceAdapter;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.RuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.context.ShardingRuntimeContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.spi.NewInstanceServiceLoader;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.apache.shardingsphere.underlying.common.yaml.engine.YamlEngine;
import org.apache.shardingsphere.underlying.merge.engine.ResultProcessEngine;
import org.apache.shardingsphere.underlying.rewrite.context.SQLRewriteContextDecorator;
import org.apache.shardingsphere.underlying.route.decorator.RouteDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-01-26 17:18
 * ************************************
 */
public class ShardingDataSource extends AbstractDataSourceAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ShardingDataSource.class);
    
    private ShardingRuntimeContext runtimeContext;
    private final Listener configListener;
    private final String groupId;
    private final String dataId;
    private final Properties properties;
    private ConfigService configService = null;
    
    static {
        NewInstanceServiceLoader.register(RouteDecorator.class);
        NewInstanceServiceLoader.register(SQLRewriteContextDecorator.class);
        NewInstanceServiceLoader.register(ResultProcessEngine.class);
    }
    /*private final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(1), new NamedThreadFactory("sentinel-nacos-ds-update"),
            new ThreadPoolExecutor.DiscardOldestPolicy());*/
    
    /**
     * {@link ShardingRule} 解析并封装分表，主从配置
     */
    public ShardingDataSource(final Map<String, DataSource> dataSourceMap, final ShardingRule shardingRule, final Properties props,
                              final Properties properties, final String groupId, final String dataId) throws SQLException {
        super(dataSourceMap);
        checkDataSourceType(dataSourceMap);
        runtimeContext = new ShardingRuntimeContext(dataSourceMap, shardingRule, props, getDatabaseType());
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(dataId)) {
            throw new IllegalArgumentException(String.format("Bad argument: groupId=[%s], dataId=[%s]",
                    groupId, dataId));
        }
        Assert.notNull(properties, "Nacos properties must not be null, you could put some keys from PropertyKeyConst");
        this.groupId = groupId;
        this.dataId = dataId;
        this.properties = properties;
        this.configListener = new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }
            
            @Override
            public void receiveConfigInfo(final String configInfo) {
                // nacos配置中心的数据转换为sharding配置
                ShardingRuleConfiguration ruleConfiguration = new ShardingRuleConfigurationYamlSwapper().swap(
                        YamlEngine.unmarshal(configInfo, YamlShardingRuleConfiguration.class, new YamlRootShardingConfigurationConstructor()));
                
                try {
                    runtimeContext = new ShardingRuntimeContext(getDataSourceMap(), new ShardingRule(ruleConfiguration, getDataSourceMap().keySet()),
                            props, getDatabaseType());
                } catch (SQLException e) {
                    logger.error("create runtimeContext error", e);
                }
            }
        };
        initNacosListener();
        loadInitConfig();
    }
    
    /**
     * # 命名格式为驼峰式，不支持横线分隔。配置参数项参考 YamlShardingRuleConfiguration
     * tables:
     *   pm_business_opt_log:
     *     actualDataNodes: ds0.pm_business_opt_log,ds0.pm_business_opt_log_$->{2020}
     *     tableStrategy:
     *       standard:
     *         # 分片字段
     *         shardingColumn: create_time
     *         # eq，in匹配算法类
     *         preciseAlgorithmClassName: com.vfuchong.sptsm.admin.sharding.DatePreciseShardingAlgorithm
     *         # range范围匹配算法类（date between and）
     *         rangeAlgorithmClassName: com.vfuchong.sptsm.admin.sharding.DateRangeShardingAlgorithm
     * bindingTables:
     *   - pm_business_opt_log
     * masterSlaveRules:
     *   ds0:
     *     # 主库名，上面的datasource.names配置
     *     masterDataSourceName: master
     *     # 从库列表，多个使用逗号分隔
     *     slaveDataSourceNames:
     *       - slave
     */
    private void initNacosListener() {
        try {
            this.configService = NacosFactory.createConfigService(this.properties);
            // Add config listener.
            configService.addListener(dataId, groupId, configListener);
        } catch (Exception e) {
            logger.error("add nacos listener error", e);
        }
    }
    
    private void loadInitConfig() {
        try {
            if (configService == null) {
                throw new IllegalStateException("Nacos config service has not been initialized or error occurred");
            }
            String configValue = configService.getConfig(dataId, groupId, 2000L);
            if (configValue == null) {
                logger.warn("nacos config is null");
                return;
            }
            ShardingRuleConfiguration ruleConfiguration = new ShardingRuleConfigurationYamlSwapper().swap(
                    YamlEngine.unmarshal(configValue, YamlShardingRuleConfiguration.class, new YamlRootShardingConfigurationConstructor()));
            runtimeContext = new ShardingRuntimeContext(getDataSourceMap(), new ShardingRule(ruleConfiguration, getDataSourceMap().keySet()),
                    this.runtimeContext.getProperties().getProps(), getDatabaseType());
        } catch (Exception ex) {
            logger.error("load init sharding config error", ex);
        }
    }
    
    private void checkDataSourceType(final Map<String, DataSource> dataSourceMap) {
        for (DataSource each : dataSourceMap.values()) {
            Preconditions.checkArgument(!(each instanceof MasterSlaveDataSource), "Initialized data sources can not be master-slave data sources.");
        }
    }
    
    @Override
    public final ShardingConnection getConnection() {
        return new ShardingConnection(getDataSourceMap(), runtimeContext, TransactionTypeHolder.get());
    }
    
    @Override
    public RuntimeContext<ShardingRule> getRuntimeContext() {
        return runtimeContext;
    }
    
    public static void main(String[] args) {
        String text = "tables:\n" +
                "  pm_business_opt_log:\n" +
                "    actualDataNodes: master.pm_business_opt_log,master.pm_business_opt_log_$->{2019..2021}\n" +
                "    tableStrategy:\n" +
                "      standard:\n" +
                "        # 分片字段\n" +
                "        shardingColumn: create_time\n" +
                "        # eq，in匹配算法类\n" +
                "        preciseAlgorithmClassName: com.zz.upms.admin.sharding.DatePreciseShardingAlgorithm\n" +
                "        # range范围匹配算法类（date between and）\n" +
                "        rangeAlgorithmClassName: com.zz.upms.admin.sharding.DateRangeShardingAlgorithm\n" +
                "bindingTables:\n" +
                "  - pm_business_opt_log";
        YamlShardingRuleConfiguration ruleConfiguration = YamlEngine.unmarshal(text, YamlShardingRuleConfiguration.class,
                new YamlRootShardingConfigurationConstructor());
        System.out.println(ruleConfiguration.getBindingTables());
    }
}
