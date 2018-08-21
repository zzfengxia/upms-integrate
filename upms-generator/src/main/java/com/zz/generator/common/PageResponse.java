package com.zz.generator.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-13 16:41
 * @desc 分页响应
 * ************************************
 */
@Data
public class PageResponse<T> implements Serializable {
    /**
     * 数据行
     */
    private T rows;
    /**
     * 总行数
     */
    private int total;

    public PageResponse() {
    }

    public PageResponse(T rows, int total) {
        this.rows = rows;
        this.total = total;
    }
}
