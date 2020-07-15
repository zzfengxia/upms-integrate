package com.zz.upms.admin.web.controller.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public ShiroDbRealm.ShiroUser getCurUser() {
        return (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
    }

    public PageResponse<?> wrapperPageResult(Page result) {
        return PageResponse.result(result.getRecords(), (int) result.getTotal());
    }

    public void saveSessionAttr(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    public Object getSessionAttr(Object key) {
        return getSession().getAttribute(key);
    }

    private Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }
}
