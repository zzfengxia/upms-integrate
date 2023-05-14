package com.zz.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-05-12 16:09
 * @desc JDK动态代理使用DEMO
 * ************************************
 */
public class UserDaoProxyHandler implements InvocationHandler {
    // 代理目标对象
    private Object target;

    public UserDaoProxyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 执行入口
        System.out.println("开始执行代理方法");
        System.out.println("调用方法名：" + method.getName());
        // 执行目标对象指定方法
        Object result = method.invoke(target, args);
        System.out.println("结束执行代理方法");
        return result;
    }

    public static void main(String[] args) {
        UserDao userDao = () -> {};
        UserDaoProxyHandler userDaoProxyHandler = new UserDaoProxyHandler(userDao);

        UserDao userDaoProxy = (UserDao) Proxy.newProxyInstance(
                userDao.getClass().getClassLoader(),
                userDao.getClass().getInterfaces(),
                userDaoProxyHandler);
        userDaoProxy.message();
    }
}
