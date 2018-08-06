package com.zz.upms.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan(basePackages = {"com.zz.upms.base.dao"})
@ComponentScan(basePackages = {"com.zz.upms"})
public class UpmsAdminApplicationTests {

    @Test
    public void contextLoads() {
    }

}
