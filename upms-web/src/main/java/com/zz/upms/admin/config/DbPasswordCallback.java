package com.zz.upms.admin.config;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.DruidPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * ************************************
 * create by Intellij IDEA
 * Druid多数据源场景数据库密码解密方法实现
 * 在Druid多数据源场景下，Druid不会自动解密数据库密码
 *
 * @author Francis.zz
 * @date 2019-11-14 16:35
 * ************************************
 */
public class DbPasswordCallback extends DruidPasswordCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbPasswordCallback.class);

    /**
     * 自定义数据库密钥解密。
     *
     * @param properties 只能识别 connection-properties属性中配置
     */
    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String password = (String) properties.get("config.decrypt.password");
        String publickey = (String) properties.get("config.decrypt.key");
        try {
            String dbpassword = ConfigTools.decrypt(publickey, password);
            setPassword(dbpassword.toCharArray());
        } catch (Exception e) {
            LOGGER.error("Druid ConfigTools.decrypt", e);
        }
    }

    public static void main(String[] args) throws Exception {
        String passwordPlain = "111";
        String[] arr = ConfigTools.genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + ConfigTools.encrypt(arr[0], passwordPlain));
    }
}
