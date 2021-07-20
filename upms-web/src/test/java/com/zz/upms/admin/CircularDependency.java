package com.zz.upms.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2021-07-12 17:45
 * ************************************
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CircularDependency {

    @Autowired
    private ClassA a;

    @Test
    public void testInit() {
        a.show();
    }

}
