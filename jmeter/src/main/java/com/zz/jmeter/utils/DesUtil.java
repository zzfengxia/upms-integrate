package com.zz.jmeter.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Created by Francis.zz on 2016-04-27.
 * 描述：java DES，3DES加解密实现，3DES加密的密钥必须大于24个字节 <br/>
 */
public class DesUtil {
    public static final byte[] ZERO_IVC = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    public DesUtil() {
    }

    public static byte[] create3DESKey() throws GeneralSecurityException {
        KeyGenerator kg = KeyGenerator.getInstance("DESede");
        kg.init(112);
        byte[] key24 = kg.generateKey().getEncoded();
        byte[] result = new byte[16];
        System.arraycopy(key24, 0, result, 0, 16);
        return result;
    }

    public static byte[] encryptBy3DesCbc(byte[] content, byte[] key, byte[] ivb) throws GeneralSecurityException {
        byte[] _3deskey = new byte[24];
        System.arraycopy(key, 0, _3deskey, 0, 16);
        System.arraycopy(key, 0, _3deskey, 16, 8);
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        SecretKey secureKey = new SecretKeySpec(_3deskey, "DESede");
        IvParameterSpec iv = new IvParameterSpec(ivb);
        cipher.init(1, secureKey, iv);
        return cipher.doFinal(content);
    }

    public static byte[] decryptBy3DesCbc(byte[] content, byte[] key, byte[] ivb) throws GeneralSecurityException {
        byte[] _3deskey = new byte[24];
        System.arraycopy(key, 0, _3deskey, 0, 16);
        System.arraycopy(key, 0, _3deskey, 16, 8);
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        SecretKey secureKey = new SecretKeySpec(_3deskey, "DESede");
        IvParameterSpec iv = new IvParameterSpec(ivb);
        cipher.init(2, secureKey, iv);
        return cipher.doFinal(content);
    }

    public static byte[] encryptBy3DesCbc(byte[] content, byte[] key) throws GeneralSecurityException {
        return encryptBy3DesCbc(content, key, ZERO_IVC);
    }

    public static byte[] decryptBy3DesCbc(byte[] content, byte[] key) throws GeneralSecurityException {
        return decryptBy3DesCbc(content, key, ZERO_IVC);
    }

    public static byte[] encryptBy3DesEcb(byte[] content, byte[] key) throws GeneralSecurityException {
        byte[] _3deskey = new byte[24];
        System.arraycopy(key, 0, _3deskey, 0, 16);
        System.arraycopy(key, 0, _3deskey, 16, 8);
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        SecretKey secureKey = new SecretKeySpec(_3deskey, "DESede");
        cipher.init(1, secureKey);
        return cipher.doFinal(content);
    }

    public static int encryptBy3DesEcb(byte[] key, byte[] content, int inputOffset, int inputLen, byte[] output, int outputOffset) throws Exception {
        SecretKey secretKey = createSecretKey(key, key.length);
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(1, secretKey);
        return cipher.doFinal(content, inputOffset, inputLen, output, outputOffset);
    }

    public static byte[] decryptBy3DesEcb(byte[] content, byte[] key) throws GeneralSecurityException {
        byte[] _3deskey = new byte[24];
        System.arraycopy(key, 0, _3deskey, 0, 16);
        System.arraycopy(key, 0, _3deskey, 16, 8);
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        SecretKey secureKey = new SecretKeySpec(_3deskey, "DESede");
        cipher.init(2, secureKey);
        return cipher.doFinal(content);
    }

    public static int decryptBy3DesEcb(byte[] key, byte[] content, int inputOffset, int inputLen, byte[] output, int outputOffset) throws Exception {
        SecretKey secretKey = createSecretKey(key, key.length);
        Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
        cipher.init(2, secretKey);
        return cipher.doFinal(content, inputOffset, inputLen, output, outputOffset);
    }

    public static byte[] encryptByDesCbc(byte[] content, byte[] key) throws GeneralSecurityException {
        return encryptByDesCbc(content, key, ZERO_IVC);
    }

    public static byte[] decryptByDesCbc(byte[] content, byte[] key) throws GeneralSecurityException {
        return decryptByDesCbc(content, key, ZERO_IVC);
    }

    public static byte[] encryptByDesCbc(byte[] content, byte[] key, byte[] icv) throws GeneralSecurityException {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(icv);
        cipher.init(1, secretKey, iv, sr);
        return cipher.doFinal(content);
    }

    public static byte[] decryptByDesCbc(byte[] content, byte[] key, byte[] icv) throws GeneralSecurityException {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(icv);
        cipher.init(2, secretKey, iv, sr);
        return cipher.doFinal(content);
    }

    public static byte[] encryptByDesEcb(byte[] content, byte[] key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(new DESKeySpec(key));
        cipher.init(1, secretKey);
        return cipher.doFinal(content);
    }

    public static byte[] decryptByDesEcb(byte[] content, byte[] key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(new DESKeySpec(key));
        cipher.init(2, secretKey);
        return cipher.doFinal(content);
    }

    public static byte[] encryptBy3DesCbcLast8Mac(byte[] content, byte[] key) throws GeneralSecurityException {
        byte[] edata = encryptBy3DesCbc(content, key);
        byte[] result = new byte[8];
        System.arraycopy(edata, edata.length - 8, result, 0, 8);
        return result;
    }

    public static byte[] xOr(byte[] b1, byte[] b2) {
        byte[] tXor = new byte[Math.min(b1.length, b2.length)];

        for(int i = 0; i < tXor.length; ++i) {
            tXor[i] = (byte)(b1[i] ^ b2[i]);
        }

        return tXor;
    }

    public static void int2byte(int n, byte[] buf, int offset) {
        buf[offset] = (byte)(n >> 24);
        buf[offset + 1] = (byte)(n >> 16);
        buf[offset + 2] = (byte)(n >> 8);
        buf[offset + 3] = (byte)n;
    }

    public static void long2byte(long n, byte[] buf, int offset) {
        buf[offset] = (byte)((int)(n >> 56));
        buf[offset + 1] = (byte)((int)(n >> 48));
        buf[offset + 2] = (byte)((int)(n >> 40));
        buf[offset + 3] = (byte)((int)(n >> 32));
        buf[offset + 4] = (byte)((int)(n >> 24));
        buf[offset + 5] = (byte)((int)(n >> 16));
        buf[offset + 6] = (byte)((int)(n >> 8));
        buf[offset + 7] = (byte)((int)n);
    }

    public static SecretKey createSecretKey(byte[] key, int keyLen) throws Exception {
        String algorithm;
        Object keySpec;
        byte[] finalKey;
        switch(keyLen) {
            case 8:
                algorithm = "DES";
                finalKey = key;
                if (key.length > 8) {
                    finalKey = new byte[8];
                    System.arraycopy(key, 0, finalKey, 0, 8);
                }

                keySpec = new DESKeySpec(finalKey, 0);
                break;
            case 16:
                algorithm = "DESede";
                byte[] fKey = new byte[24];
                System.arraycopy(key, 0, fKey, 0, 16);
                System.arraycopy(key, 0, fKey, 16, 8);
                keySpec = new DESedeKeySpec(fKey, 0);
                break;
            case 24:
                algorithm = "DESede";
                finalKey = key;
                if (key.length > 24) {
                    finalKey = new byte[24];
                    System.arraycopy(key, 0, finalKey, 0, 24);
                }

                keySpec = new DESedeKeySpec(finalKey, 0);
                break;
            default:
                throw new RuntimeException("invalid length of rawKey");
        }

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
        return keyFactory.generateSecret((KeySpec)keySpec);
    }

    public static String desCbcEncode(String data, String key) {
        try {
            DESKeySpec keySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key privatekey = keyFactory.generateSecret(keySpec);
            Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ZERO_IVC);
            enCipher.init(1, privatekey, iv);
            byte[] pasByte = enCipher.doFinal(data.getBytes());
            BASE64Encoder base64Encoder = new BASE64Encoder();
            return base64Encoder.encode(pasByte);
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static String desCbcDecode(String data, String key) {
        try {
            DESKeySpec keySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key privatekey = keyFactory.generateSecret(keySpec);
            Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ZERO_IVC);
            deCipher.init(2, privatekey, iv);
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));
            return new String(pasByte);
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    /**
     * 字节取反操作
     *
     * @param factor 分散因子
     * @return
     */
    public static byte[] antiOperation(byte[] factor) {
        for (int i = 0; i < factor.length; i++) {
            int b = 0;
            for (int j = 0; j < 8; j++) {
                int bit = (factor[i] >> j & 1) == 0 ? 1 : 0;
                b += (1 << j) * bit;
            }
            factor[i] = (byte) b;
        }
        return factor;
    }
}
