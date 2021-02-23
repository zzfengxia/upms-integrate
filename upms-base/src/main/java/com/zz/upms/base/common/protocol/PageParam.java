package com.zz.upms.base.common.protocol;

import lombok.Data;

import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-22 17:26
 * @desc bootstrap-table 分页请求
 * ************************************
 */
@Data
public class PageParam<T> {
    /**
     * 搜索参数
     */
    private String search;
    /**
     * 排序规则asc|desc
     */
    private String order;
    /**
     * 起始数。默认0 pageSize * (pageNumber - 1)
     */
    private int offset;
    /**
     * 每页数量
     */
    private int limit;
    
    /**
     * 排序字段
     */
    private String sort;
    
    /**
     * 排序字段组
     */
    private List<String> sorts;
    
    /**
     * 额外查询参数
     */
    private T params;
}
