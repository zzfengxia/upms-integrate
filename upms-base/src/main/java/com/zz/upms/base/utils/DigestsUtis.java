package com.zz.upms.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Francis.zz on 2017/4/22.
 * 密码摘要加密
 */
public class DigestsUtis {
    private static final Logger log = LoggerFactory.getLogger(DigestsUtis.class);

    public static final String CRYP_ALGORITHM = "MD5";      // 加密算法
    public static final int CRYP_ITERATIONS = 512;          // 迭代次数
    public static final int SALT_SIZE = 8;                  // salt字节数

    private static SecureRandom random = new SecureRandom();

    /**
     * 生成密文
     * @param plaintext 明文
     * @param salt 附加信息(Hex)
     * @return
     */
    public static String encrypt(String plaintext, String salt) {
        byte[] bSalt = HexUtil.hexToByteArray(salt);
        byte[] password = encrypt(plaintext, bSalt, CRYP_ITERATIONS);

        return HexUtil.encodeHexStr(password);
    }

    /**
     * 明文加密
     * @param plaintext 明文
     * @param salt 附加信息
     * @param iterations 迭代次数
     */
    public static byte[] encrypt(String plaintext, byte[] salt, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(CRYP_ALGORITHM);
            if(null != salt) {
                digest.update(salt);
            }
            byte[] result = digest.digest(plaintext.getBytes());
            for(int i = 1; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }

            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 生成指定字节数的salt
     * @param size
     */
    public static byte[] getSalt(int size) {
        if(size < 1) {
            return null;
        }
        byte[] salt = new byte[size];
        random.nextBytes(salt);

        return salt;
    }

    public static String getSalt() {
        return HexUtil.encodeHexStr(getSalt(SALT_SIZE));
    }

    public static void main(String[] args) {
        // 超级管理员账户
        System.out.println(encrypt("123456", "5B76D10D95B931B0"));
    }
}
