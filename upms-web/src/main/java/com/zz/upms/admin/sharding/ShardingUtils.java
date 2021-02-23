package com.zz.upms.admin.sharding;

import com.zz.upms.base.utils.DateUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-02-02 16:48
 * ************************************
 */
public class ShardingUtils {
    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    /**
     * 按年分表
     *
     * @param lowerSuffix
     * @param upperSuffix
     * @return
     */
    public static Set<String> getSuffixListForRange(String lowerSuffix, String upperSuffix) {
        TreeSet<String> suffixList = new TreeSet<>();
        if (lowerSuffix.equals(upperSuffix)) { //上下界在同一张表
            suffixList.add(lowerSuffix);
        } else {  //上下界不在同一张表  计算间隔的所有表
            String tempSuffix = lowerSuffix;
            while (!tempSuffix.equals(upperSuffix)) {
                suffixList.add(tempSuffix);
                Date tempDate = DateUtil.string2date(tempSuffix, "yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(tempDate);
                cal.add(Calendar.YEAR, 1);
                tempSuffix = DateUtil.date2String(cal.getTime()).substring(0, 4);
            }
            suffixList.add(tempSuffix);
        }
        return suffixList;
    }
    
    public static String getYearFromDate(String date, String dateFormat) {
        try {
            Date parseDate = DateUtils.parseDate(date, dateFormat);
            return  date.substring(0, 4);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式不正确：" + date);
        }
    }
    
    public static String getYearFromDate(String date) {
        try {
            Date parseDate = DateUtils.parseDate(date, dateFormat);
            return  date.substring(0, 4);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式不正确：" + date);
        }
    }
}
