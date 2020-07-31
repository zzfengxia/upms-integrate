package com.zz.mq.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-03-10 11:04
 * ************************************
 */
@Data
public class MqMsgLog implements Serializable {
    /**
     * 消息唯一标识
     */
    private String msgId;

    /**
     * 消息体, json格式化
     */
    private String msgText;
    
    /**
     * 消息头
     */
    private String msgHeader;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 路由键
     */
    private String routingKey;

    /**
     * 状态: 0投递中 1投递成功 2投递失败 3已消费
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer tryCount;

    /**
     * 下一次重试时间
     */
    private Date nextTryTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}