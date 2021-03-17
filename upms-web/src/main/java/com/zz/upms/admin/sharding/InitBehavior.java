package com.zz.upms.admin.sharding;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-03-16 15:35
 * ************************************
 */
public class InitBehavior implements InitFunc {
    @Override
    public void init() {
        System.out.println("i'm initBehavior");
    }
}
