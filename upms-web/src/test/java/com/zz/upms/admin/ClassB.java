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
public class ClassB {
    /*public ClassB(ClassC c) {
    }*/

    private ClassC c;

    @Autowired
    public void setC(ClassC c) {
        this.c = c;
    }

    @Transactional
    public void show() {
        c.show();
        System.out.println("class b show...");
    }
}
