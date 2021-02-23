package com.zz.upms.admin.sharding;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-01-14 15:23
 * ************************************
 */
public class DatePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> availableTableNames, PreciseShardingValue<String> shardingValue) {
        String date = shardingValue.getValue();
        String year = ShardingUtils.getYearFromDate(date);
        for (String tableName : availableTableNames) {
            if (tableName.endsWith(year)) {
                return tableName;
            }
        }
        // 无后缀表
        String noSuffixTable = null;
        String maxDateTable = null;
        String minDateTable = null; // 下界表
        String logicTableName = shardingValue.getLogicTableName();
        
        for (String tableName : availableTableNames) {
            if(logicTableName.equals(tableName)) {
                noSuffixTable = logicTableName;
            } else {
                maxDateTable = (maxDateTable != null && tableName.compareTo(maxDateTable) < 0) ? maxDateTable : tableName;
                minDateTable = (minDateTable != null && tableName.compareTo(minDateTable) > 0) ? minDateTable : tableName;
            }
        }
        // 判断查询日期是否分布在无后缀的最新数据表中
        if(maxDateTable != null && year.compareTo(maxDateTable.substring(maxDateTable.length() - year.length())) > 0) {
            return noSuffixTable;
        }
        // 判断查询日期是否小于下界表日期
        if(minDateTable != null && year.compareTo(minDateTable.substring(minDateTable.length() - year.length())) < 0) {
            return minDateTable;
        }
        throw new IllegalArgumentException("未找到匹配的数据表");
    }
}
