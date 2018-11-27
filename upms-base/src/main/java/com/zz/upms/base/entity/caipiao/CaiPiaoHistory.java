package com.zz.upms.base.entity.caipiao;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

/**
 * ************************************
 * create by Code-Generator-0.0.1
 * @author Francis.zz
 * @date   2018-11-27 16:05:22
 * @desc   PiaoHistory
 * ************************************
 */
@Getter
@Setter
@TableName("cai_piao_history")
public class CaiPiaoHistory implements Serializable {
    @TableId
    private Integer id;
    private String name;
    /**
	 * 期数代码
     */
    private String code;
    /**
	 * 日期
     */
    private String date;
    /**
	 * 奖池金额(元)
     */
    private BigDecimal poolmoney;
    /**
	 * 销售注数
     */
    private Integer sales;
    private String content;
    /**
	 * 红球
     */
    private String red;
    /**
	 * 篮球
     */
    private String blue;
    private String blue2;
    /**
	 * 中奖信息
     */
    private String prizegrades;
    private String remark;

}