package com.zz.invocation;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-06-01 19:49
 * @desc CGLib动态代理使用DEMO
 * ************************************
 */
public class CGLibProxyDemo {
    private static Object createProxy(Object target) {
        // 创建Enhancer对象
        Enhancer enhancer = new Enhancer();

        // 设置目标类为父类
        enhancer.setSuperclass(target.getClass());

        // 设置回调方法
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            System.out.println("Before method: " + method.getName());

            // 调用目标方法
            Object result = proxy.invokeSuper(obj, args);

            System.out.println("After method: " + method.getName());
            return result;
        });

        // 创建代理对象
        return enhancer.create();
    }

    public static void main(String[] args) {
        // 创建目标对象
        UserService userService = new UserService();

        // 创建CGLib代理对象
        UserService proxy = (UserService) createProxy(userService);

        // 调用代理对象的方法
        proxy.say();
        proxy.perform();
    }
}



