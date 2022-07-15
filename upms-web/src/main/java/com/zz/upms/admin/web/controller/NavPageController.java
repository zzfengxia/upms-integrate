package com.zz.upms.admin.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Francis.zz on 2017/4/21.
 */
@Controller
public class NavPageController implements InitializingBean {
    Logger log = LoggerFactory.getLogger(NavPageController.class);

    @Value("${spring.application.name}")
    private String name;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(name);
    }

    @RequestMapping("login")
    public String login() {
        return "login";
    }

    @RequestMapping("views/{module}/{url}")
    public String module(@PathVariable("module") String module, @PathVariable("url") String url){
        return "views/" + module + "/" + url;
    }

    @RequestMapping(value = {"/", "index"})
    public String index() {
        return "index";
    }

    @RequestMapping("main")
    public String main() {
        return "main";
    }

    @RequestMapping("404")
    public String notFound() {
        return "views/error/404";
    }

    @RequestMapping("403")
    public String forbidden() {
        return "views/error/403";
    }

    @RequestMapping("500")
    public String error() {
        return "views/error/500";
    }
}
