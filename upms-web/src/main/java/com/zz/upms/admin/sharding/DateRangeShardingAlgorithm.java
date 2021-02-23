package com.zz.upms.admin.sharding;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-01-14 15:23
 * ************************************
 */
public class DateRangeShardingAlgorithm implements RangeShardingAlgorithm<String> {
    @Override
    public Collection<String> doSharding(Collection<String> availableTableNames, RangeShardingValue<String> rangeShardingValue) {
        // 如果路由表中有一个不带日期后缀的表，则这张表存放的是带日期后缀的最大日期至今的所有数据
        if(availableTableNames.size() == 1) {
            return availableTableNames;
        }
        
        List<String> list = new ArrayList<>();
        
        Range<String> valueRange = rangeShardingValue.getValueRange();
        String logicTableName = rangeShardingValue.getLogicTableName();
        // 范围查询必须要有上下界，不然会报错
        String lowerDate = valueRange.lowerEndpoint();
        String upperDate = valueRange.upperEndpoint();
        String lowerSuffix = ShardingUtils.getYearFromDate(lowerDate);
        String upperSuffix = ShardingUtils.getYearFromDate(upperDate);
        Set<String> rangeTableSuffix = ShardingUtils.getSuffixListForRange(lowerSuffix, upperSuffix);
        // 无后缀表
        String noSuffixTable = null;
        String maxDateTable = null;
        String minDateTable = null; // 下界表
        for (String tableName : availableTableNames) {
            if (containTableName(rangeTableSuffix, tableName)) {
                list.add(tableName);
            }
            if(logicTableName.equals(tableName)) {
                noSuffixTable = logicTableName;
            } else {
                maxDateTable = (maxDateTable != null && tableName.compareTo(maxDateTable) < 0) ? maxDateTable : tableName;
                minDateTable = (minDateTable != null && tableName.compareTo(minDateTable) > 0) ? minDateTable : tableName;
            }
        }
        String maxDate = ShardingUtils.getYearFromDate(upperDate);
        String minDate = ShardingUtils.getYearFromDate(lowerDate);
        // 判断查询日期上限是否分布在无后缀的最新数据表中
        if(maxDateTable != null && noSuffixTable != null && maxDate.compareTo(maxDateTable.substring(maxDateTable.length() - maxDate.length())) > 0) {
            list.add(noSuffixTable);
        }
        // 判断查询日期是否小于下界表日期
        if(minDateTable != null && minDate.compareTo(minDateTable.substring(minDateTable.length() - minDate.length())) < 0) {
            list.add(minDateTable);
        }
        return list;
    }
    
    private boolean containTableName(Set<String> suffixList, String tableName) {
        boolean flag = false;
        for (String s : suffixList) {
            if (tableName.endsWith(s)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
