package com.zz.upms.base.service.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Francis.zz on 2017/5/2.
 */
public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取当前登录的用户
     * @return
     */
    public ShiroDbRealm.ShiroUser getCurrentUser() {

        return (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
    }

    public Page<T> queryWithPage(PageParam params, Wrapper<T> wrapper) {

        return page(getPage(params), wrapper);
    }

    private Page<T> getPage(PageParam params) {
        // 当前页
        int curPage = params.getLimit() == 0 ? 1 : params.getOffset() / params.getLimit() + 1;
        // 排序字段，默认使用
        List<String> sortCols = params.getSorts();

        // 排序规则
        boolean isAsc = "asc".equalsIgnoreCase(params.getOrder());
    
        Page<T> pageParam = new Page<T>(curPage, params.getLimit());
        if(sortCols == null || sortCols.isEmpty()) {
            if(StringUtils.isNotEmpty(params.getSort())) {
                pageParam.addOrder(isAsc ? OrderItem.asc(params.getSort()) : OrderItem.desc(params.getSort()));
            }
        } else {
            if(StringUtils.isNotEmpty(params.getSort()) && !sortCols.contains(params.getSort())) {
                sortCols.add(params.getSort());
            }
    
            sortCols.forEach(col -> {
                pageParam.addOrder(isAsc ? OrderItem.asc(col) : OrderItem.desc(col));
            });
        }
        return pageParam;
    }

    public PageResponse<?> wrapperPageResult(Page<T> result) {
        return PageResponse.result(result.getRecords(), (int) result.getTotal());
    }
}
