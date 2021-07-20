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
public class ClassA {
    /*public ClassA(ClassB b) {
    }*/
    private ClassB b;

    @Autowired
    public void setB(ClassB b) {
        this.b = b;
    }

    @Transactional
    public void show() {
        b.show();
        System.out.println("class a show...");
    }
}
