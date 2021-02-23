package com.zz.upms.base.entity.platform;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * ************************************
 * create by Code-Generator-0.0.1
 * @author 郑天翔-tianxiang2017@gmail.com
 * @date   2019-02-14 10:29:05
 * @desc   BusinessOptLog
 * ************************************
 */
@Getter
@Setter
@TableName("pm_business_opt_log")
public class BusinessOptLog implements Serializable {
    @TableId
    private Long id;
    /**
	 * 卡片代码
     */
    private String cardCode;
    private String sourceChnl;
    /**
	 * seId
     */
    private String seUid;
    /**
	 * 业务代码(0: 业务1: 前置)
     */
    private Integer businessType;
    /**
	 * 命令Id(下载安装、个人化、充值、删卡)
     */
    private String commandId;
    /**
	 * 执行时间(单位: 毫秒)
     */
    private Integer executeTime;
    /**
	 * 执行步骤ID
     */
    private Integer stepId;
    /**
	 * 是否最后一步(0: 否 1: 是)
     */
    private Integer isLastStep;
    /**
	 * 错误码,0:正常结束
     */
    private String optCode;
    /**
	 * 错误信息
     */
    private String optMsg;
    /**
	 * 订单号(可为空)
     */
    private String orderNo;
    private String cardNo;
    private String traceId;
    /**
	 * 备注
     */
    private String remark;
    /**
	 * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createTime;
    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date updateTime;
}