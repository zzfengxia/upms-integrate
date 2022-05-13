package com.zz.mq.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ************************************
 * create by Code-Generator-0.0.1
 * @author Francis.zz
 * @date   2020-12-30 16:44:51
 * @desc   SpOrderRecharge
 * ************************************
 */
@Getter
@Setter
public class SpOrderRecharge implements Serializable {
    /**
	 * 订单号
     */
    private String orderNo;
    /**
	 * sp请求一卡通流水号
     */
    private String spReqSerial;
    /**
	 * sp请求一卡通确认流水号
     */
    private String spCfmSerial;
    /**
	 * sp发起交易时间 yyyyMMddHHmmss
     */
    private String spTransTime;
    /**
	 * 一卡通请求流水号(一卡通响应值)
     */
    private String yktReqSerial;
    /**
	 * 一卡通确认流水号(一卡通响应值)
     */
    private String yktCfmSerial;
    /**
	 * 一卡通响应交易时间；yyyyMMddHHmmss
     */
    private String yktTransTime;
    /**
	 * 交易终端编号
     */
    private String terminalNo;
    /**
	 * 卡脱机计数
     */
    private String offlineCounter;
    /**
	 * 联机交易序号
     */
    private String onlineCounter;
    /**
	 * 交易TAC
     */
    private String tac;
    /**
	 * 卡片0015文件
     */
    private String cardFile0015;
    /**
	 * 一卡通订单号
     */
    private String yktOrder;
    /**
	 * 写卡结果
     */
    private String writeResult;
    /**
	 * 上一笔充值记录
     */
    private String lastRechargeRecord;
    /**
	 * 获取mac2密钥的次数
     */
    private Integer reqKeyNum;
    /**
	 * 票卡余额（交易前金额）
     */
    private BigDecimal cardBalance;
    /**
     * 批次号，部分厂商需要使用该字段统计数量
     */
    private String batchNo;
    /**
     * 充值扩展字段，json格式
     * @see {@link com.zz.mq.entity.RechargeExtData}
     */
    private String extJsonData;
    private RechargeExtData extData = new RechargeExtData();
    /**
	 * 创建时间，yyyyMMdd HH:mm:ss
     */
    private Date createTime;
    /**
	 * 修改时间
     */
    private Date updateTime;

    /**
     * 非数据库字段  是否增加请求mac2次数
     */
    private boolean isAddKeyNum;

    public SpOrderRecharge() {
    }

    public SpOrderRecharge(String orderNo) {
        this.orderNo = orderNo;
    }
}