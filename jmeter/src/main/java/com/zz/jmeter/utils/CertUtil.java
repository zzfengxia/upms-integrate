package com.zz.jmeter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-12-10 11:36
 * ************************************
 */
public class CertUtil {
    private final static Logger logger = LoggerFactory.getLogger(CertUtil.class);

    private final static String KEY_STORE_TYPE_JKS = "jks";         // keystore，jks证书
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";        // .p12后缀的证书

    /** https双向认证证书存储Map */
    private final static Map<String, KeyStore> keyStoreMap = new ConcurrentHashMap<String, KeyStore>();

    public static KeyStore getKeyStoreByCert(String certFilePath, String certPwd) {
        return getKeyStoreByCert(certFilePath, certPwd, getKstype(certFilePath));
    }

    public static KeyStore getKeyStoreByCert(String certFilePath, String certPwd, String type) {
        FileInputStream trustStoreFile = null;
        try {
            KeyStore keyStore = keyStoreMap.get(certFilePath);
            if (keyStore != null) {
                return keyStore;
            }
            trustStoreFile = new FileInputStream(new File(certFilePath));
            keyStore = KeyStore.getInstance(type);
            keyStore.load(trustStoreFile, certPwd.toCharArray());
            // 缓存证书
            keyStoreMap.put(certFilePath, keyStore);
            return keyStore;
        } catch (Exception e) {
            logger.error("getKeyStoreByCert error", e);
            return null;
        } finally {
            if(trustStoreFile != null) {
                try {
                    trustStoreFile.close();
                } catch (IOException e) {
                    logger.error("cert file close error", e);
                }
            }
        }
    }

    /**
     * 证书文件过滤器
     *
     */
    static class CerFilter implements FilenameFilter {
        public boolean isCer(String name) {
            if (name.toLowerCase().endsWith(".cer")) {
                return true;
            } else {
                return false;
            }
        }
        public boolean accept(File dir, String name) {
            return isCer(name);
        }
    }

    public static String getKstype(String certName) {
        if (certName.endsWith(".p12")) {
            return KEY_STORE_TYPE_P12;
        }
        return KEY_STORE_TYPE_JKS;
    }
}
