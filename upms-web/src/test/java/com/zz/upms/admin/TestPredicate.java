package com.zz.upms.admin;

import java.util.function.Predicate;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-07-21 16:24
 * ************************************
 */
public class TestPredicate {
    private static final Predicate<String> isHello = (str) -> str.equals("Hello");

    public static void main(String[] args) {
        Predicate<String> isTom = (str) -> str.equals("Tom");
        Predicate<String> sure = isTom.or(isHello);

        System.out.println(sure.test("Tom"));
    }
}
