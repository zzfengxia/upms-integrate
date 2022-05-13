package com.zz.jmeter.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-05-10 15:37
 * ************************************
 */
public class RSASignatureUtil {
    public static final String SIGN_ALGORITHMS_SHA1 = "SHA1WithRSA";
    public static final String SIGN_ALGORITHMS_SHA256 = "SHA256WithRSA";

    public RSASignatureUtil() {
    }

    public static String sign(String content, String privateKey, String encode, String signAlgorithms) {
        try {
            PKCS8EncodedKeySpec priPkcs8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(priPkcs8);
            Signature signature = Signature.getInstance(signAlgorithms);
            signature.initSign(priKey);
            signature.update(content.getBytes(encode));
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static String sign(String content, String privateKey, String signAlgorithms) {
        try {
            PKCS8EncodedKeySpec priPkcs8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(priPkcs8);
            Signature signature = Signature.getInstance(signAlgorithms);
            signature.initSign(priKey);
            signature.update(content.getBytes());
            byte[] signed = signature.sign();
            return Base64.encodeBase64String(signed);
        } catch (Exception var8) {
            var8.printStackTrace();
            return null;
        }
    }

    public static boolean doCheck(String content, String sign, String publicKey, String encode, String algorithm) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(encode));
            boolean isVerify = signature.verify(Base64.decodeBase64(sign));
            return isVerify;
        } catch (Exception var10) {
            var10.printStackTrace();
            return false;
        }
    }

    public static boolean doCheck(String content, String sign, String publicKey, String algorithm) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decodeBase64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pubKey);
            signature.update(content.getBytes());
            boolean isVerify = signature.verify(Base64.decodeBase64(sign));
            return isVerify;
        } catch (Exception var9) {
            var9.printStackTrace();
            return false;
        }
    }
}
