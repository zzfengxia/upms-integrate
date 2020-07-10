package com.zz.upms.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Francis.zz on 2017/10/26.
 * 发送短信
 */
public class SMSUtils {
    private static final Logger logger = LoggerFactory.getLogger(SMSUtils.class);

    public static void sendSMS(String msg, String... phone) {
        Map<String, String> params = new HashMap<>();
        params.put("account", "83mtw4");
        params.put("pswd", "f68ciA8q");

        params.put("msg", msg);
        params.put("needstatus", "false");
        params.put("product", "");
        //params.put("extno", "");

        String uri = "https://send.18sms.com/msg/HttpBatchSendSM";
    
        for (String p : phone) {
            params.put("mobile", p);
            try {
                CustomApacheHttpClient client = new CustomApacheHttpClient(10000);
                String returnString = client.doPost(uri, params, "utf-8");
                logger.info("abnormal warning send sms phoneNum:{}, msg:{}, note result:{}", p, msg, returnString);
                System.out.println("send message....");
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("send sms note error.", e);
            }
        }
    }
    
    public static void main(String[] args) {
        sendSMS("测试demo", "18673697511");
    }
}
