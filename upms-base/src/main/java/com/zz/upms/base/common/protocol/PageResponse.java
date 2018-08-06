package com.zz.upms.base.common.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-22 17:26
 * @desc bootstrap-table 分页响应
 * ************************************
 */
@Getter
@Setter
public class PageResponse<T> {
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

    public static <T> PageResponse result(T rows, int total) {
        return new PageResponse<T>(rows, total);
    }
}
