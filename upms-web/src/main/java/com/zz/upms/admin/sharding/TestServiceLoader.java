package com.zz.upms.admin.sharding;

import java.util.ServiceLoader;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-03-16 15:38
 * ************************************
 */
public class TestServiceLoader {
    public static void main(String[] args) {
        ServiceLoader<InitFunc> initFunList = ServiceLoader.load(InitFunc.class);
        initFunList.forEach(InitFunc::init);
    }
}
