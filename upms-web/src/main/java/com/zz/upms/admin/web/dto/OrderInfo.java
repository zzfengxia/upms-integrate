package com.zz.upms.admin.web.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-04-06 17:12
 * ************************************
 */
@Data
public class OrderInfo implements Serializable {
    private String orderNo;
    private String skuName;
    private Integer price;
}
