package com.zz.upms.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-07-12 17:54
 * ************************************
 */
@Service
public class ClassC {
    /*public ClassC(ClassA a) {
    }*/

    private ClassA a;

    @Autowired
    public void setA(ClassA a) {
        this.a = a;
    }

    @Transactional
    public void show() {
        System.out.println("class c show...");
    }
}
