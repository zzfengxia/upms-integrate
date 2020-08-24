package com.zz.mq.controller;

import com.zz.mq.service.PublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-08-21 14:29
 * ************************************
 */
@Controller
public class ConcurrentPublishController {
    @Autowired
    private PublishService publishService;
    
    @GetMapping("concurrentPublish")
    @ResponseBody
    public String testConcurrentPublishMQ(Integer threadNum, Integer frequency) {
        publishService.concurrentPublish(threadNum, frequency);
        return "success";
    }
    
    
}
