package com.zz.upms.base.common.protocol;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-07-04 17:29
 * @desc 重写Page类，解决分页limit数据不足时，查询速度慢的问题
 * ************************************
 */
public class CustomPage<T> extends Page<T> {
    public CustomPage() {
    }

    public CustomPage(int current, int size) {
        super(current, size);
    }

    public CustomPage(int current, int size, String orderByField) {
        super(current, size);
        this.setOrderByField(orderByField);
    }

    public CustomPage(int current, int size, String orderByField, boolean isAsc) {
        this(current, size, orderByField);
        this.setAsc(isAsc);
    }

    /**
     * 重写该方法解决分页limit数据不足时，查询速度慢的问题。当数据不足时，limit [offset,size]的size使用数据余量替换
     *
     * @return
     */
    @Override
    public int getSize() {
        long total = super.getTotal();
        int size = super.getSize();
        int offset = PageHelper.offsetCurrent(super.getCurrent(), size);
        long remainRowSize = total - offset;

        size = remainRowSize > size ? size : (int) remainRowSize;
        return size;
    }
}
