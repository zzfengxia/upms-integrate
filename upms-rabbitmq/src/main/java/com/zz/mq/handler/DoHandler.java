package com.zz.mq.handler;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-02-15 10:46
 * ************************************
 */
@Service
public class DoHandler implements InitializingBean {
    @Autowired
    private List<IHandler> handlerList;

    public void doHandle() {
        handlerList.forEach(IHandler::handle);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.doHandle();
    }
}
