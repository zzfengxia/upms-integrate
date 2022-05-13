package com.zz.mq.handler;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-02-15 10:44
 * ************************************
 */
@Service
@Order(-9198)
public class Handler2 implements IHandler {
    @Override
    public void handle() {
        System.out.println("handler 2");
    }
}
