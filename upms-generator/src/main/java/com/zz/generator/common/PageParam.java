package com.zz.generator.common;

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
public class PageParam {
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
}
