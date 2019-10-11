package com.zz.upms.base.service.base;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zz.upms.base.common.protocol.CustomPage;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Created by Francis.zz on 2017/5/2.
 */
public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${mybatis-plus.global-config.db-column-underline:false}")
    private boolean camelCaseFlag;

    /**
     * 获取当前登录的用户
     * @return
     */
    public ShiroDbRealm.ShiroUser getCurrentUser() {

        return (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
    }

    public Page<T> queryWithPage(PageParam params, Wrapper<T> wrapper) {

        return selectPage(getPage(params), wrapper);
    }

    private Page<T> getPage(PageParam params) {
        // 当前页
        int curPage = params.getLimit() == 0 ? 1 : params.getOffset() / params.getLimit() + 1;
        // 排序字段，默认使用
        List<String> sortCols = params.getSorts();

        // 排序规则
        boolean isAsc = "asc".equalsIgnoreCase(params.getOrder());

        if(sortCols == null) {
            return new CustomPage<>(curPage, params.getLimit(), params.getSort(), isAsc);
        } else {
            if(StringUtils.isNotEmpty(params.getSort()) && !sortCols.contains(params.getSort())) {
                sortCols.add(params.getSort());
            }

            Page<T> page = new CustomPage<>(curPage, params.getLimit());
            if(isAsc) {
                page.setAscs(sortCols);
            } else {
                page.setDescs(sortCols);
            }

            return page;
        }
    }

    public PageResponse<?> wrapperPageResult(Page<T> result) {
        return PageResponse.result(result.getRecords(), (int) result.getTotal());
    }
}
