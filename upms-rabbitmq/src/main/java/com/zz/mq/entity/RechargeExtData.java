package com.zz.mq.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ************************************
 * create by Intellij IDEA
 * sp_order_recharge表扩展字段，json格式
 * @author Francis.zz
 * @date 2020-12-30 16:51
 * ************************************
 */
@Data
@Accessors(chain = true)
public class RechargeExtData {

    /**
     * 请求包号
     */
    private String reqSeq;

    /**
     * 卡押金
     */
    private String deposit;

    /**
     * 保存响应数据  (盛京通)
     */
    private String respData;

    /**
     * 月票当月余次
     */
    private String restNum;

    /**
     * 透支金额
     */
    private String overDrawNum;

    /**
     * 卡片状态
     */
    private String yktState;

    /**
     * 0005文件
     */
    private String cardFile0005;

    /**
     * 接口响应 卡主类型
     */
    private String cardMasterType;

    /**
     * 接口响应 卡子类型
     */
    private String cardSubType;

    /**
     * 随机数，金华同步到其系统时需要
     */
    private String random;

    /**
     * 授权清算日期，潍坊需要
     */
    private String authSettDate;

    private String authSeq;
    /**
     * 授权清算限制流水，潍坊需要
     */
    private String limitedAuthSeq;

    private String randomNum;

    /**
     * mac1
     */
    private String mac1;

    /**
     * 上次交易终端编号
     */
    private String lastTerminalNo;

    /**
     * 上次交易日期时间
     */
    private String lastTransTime;

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
