package com.zz.upms.admin.web.controller.system;

import com.alibaba.fastjson.JSON;
import com.zz.upms.admin.web.dto.OrderInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-04-01 17:52
 * ************************************
 */
@Controller
@RequestMapping("demo")
public class DemoController {
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(String orderNo, String name) {
        System.out.println("orderNo:" + orderNo + ", name:" + name);
        return "Hello World";
    }

    @RequestMapping(value = "/queryJson", method = RequestMethod.GET)
    @ResponseBody
    public String queryJson(@RequestParam(name = "order") String orderNo, @RequestParam(name = "name") String nameStr) {
        System.out.println("orderNo:" + orderNo + ", name:" + nameStr);
        return "Hello World";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public OrderInfo create(OrderInfo orderInfo) {
        System.out.println(JSON.toJSONString(orderInfo));
        orderInfo.setOrderNo(new Date().getTime() + "");
        return orderInfo;
    }
}
