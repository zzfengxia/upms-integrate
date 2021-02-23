package com.zz.upms.base.entity.platform;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-04-27 14:59
 * @desc BusinessOptLogVO
 * ************************************
 */
@Setter
@Getter
public class BusinessOptLogVO {
    private String cardCode;
    private String sourceChnl;
    private String seUid;
    private String cardNo;
    private Integer businessType;
    private String commandId;
    private String optCode;
    private Date startTime;
    private Date endTime;
    private Boolean groupByDay;
    private Boolean groupByModel;
    private Boolean groupByCardCode;
    // 时间分组格式
    private String unit;
    // mysql时间标识
    private String timeFlag;
    // 时间区间数
    private Integer timeRows;

    public static final String MYSQL_HOUR = "HOUR";
    public static final String MYSQL_DAY = "DAY";
    public static final String MYSQL_MONTH = "MONTH";

    public static final String UNIT_HOUR = "%Y-%m-%d %H";
    public static final String UNIT_DAY = "%Y-%m-%d";
    public static final String UNIT_MONTH = "%Y-%m";

    private String type; // open recharge

    private String deviceModel;
    private String day;

}
