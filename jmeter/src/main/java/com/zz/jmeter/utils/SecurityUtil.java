package com.zz.jmeter.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-05-12 18:14
 * ************************************
 */
public class SecurityUtil {
    public static byte[] PADDING_8LSB = new byte[]{-128, 0, 0, 0, 0, 0, 0, 0};
    public static byte[] ZERO_8BYTES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    public SecurityUtil() {
    }

    public static int generateRandom(int paramInt1, byte[] paramArrayOfByte, int paramInt2) throws Exception {
        byte[] arrayOfByte = new byte[paramInt1];
        SecureRandom localSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        localSecureRandom.nextBytes(arrayOfByte);
        System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt2, paramInt1);
        return paramInt1;
    }

    public static byte[] encryptECBNoPAD(SecretKey paramSecretKey, byte[] paramArrayOfByte, byte paramByte) throws Exception {
        String str = combineDESTransformation(paramSecretKey.getAlgorithm());
        Cipher localCipher = Cipher.getInstance(str);
        localCipher.init(paramByte, paramSecretKey);
        byte[] arrayOfByte = pad8LSB(paramArrayOfByte, (short)0, paramArrayOfByte.length);
        return arrayOfByte == null ? localCipher.doFinal(paramArrayOfByte) : localCipher.doFinal(arrayOfByte);
    }

    private static String combineDESTransformation(String paramString) {
        return paramString + "/" + "CBC" + "/" + "PKCS1Padding";
    }

    public static SecretKey createSecretKey(byte[] paramArrayOfByte) throws Exception {
        int i = (short)paramArrayOfByte.length;
        return createSecretKey(paramArrayOfByte, 0, i);
    }

    public static SecretKey createSecretKey(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws Exception {
        String str;
        Object localObject1;
        byte[] localObject2;
        switch(paramInt2) {
            case 8:
                str = "DES";
                localObject2 = paramArrayOfByte;
                if (paramArrayOfByte.length > 8) {
                    localObject2 = new byte[8];
                    System.arraycopy(paramArrayOfByte, 0, localObject2, 0, 8);
                }

                localObject1 = new DESKeySpec((byte[])((byte[])localObject2), 0);
                break;
            case 16:
                str = "DESede";
                byte[] arrayOfByte = new byte[24];
                System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 16);
                System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 16, 8);
                localObject1 = new DESedeKeySpec(arrayOfByte, 0);
                break;
            case 24:
                str = "DESede";
                localObject2 = paramArrayOfByte;
                if (paramArrayOfByte.length > 24) {
                    localObject2 = new byte[24];
                    System.arraycopy(paramArrayOfByte, 0, localObject2, 0, 24);
                }

                localObject1 = new DESedeKeySpec((byte[])((byte[])localObject2), 0);
                break;
            default:
                throw new RuntimeException("invalid length of rawKey");
        }

        Object localObject24 = SecretKeyFactory.getInstance(str);
        return ((SecretKeyFactory)localObject24).generateSecret((KeySpec)localObject1);
    }

    public static byte[] calculateMac(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        byte[] arrayOfByte1 = new byte[8];
        byte[] arrayOfByte2 = new byte[paramArrayOfByte1.length];

        try {
            encryptDES_CBC_NoPadding(paramArrayOfByte2, ZERO_8BYTES, paramArrayOfByte1, 0, paramArrayOfByte1.length, arrayOfByte2, 0);
            System.arraycopy(arrayOfByte2, arrayOfByte2.length - 8, arrayOfByte1, 0, 8);
            return arrayOfByte1;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static byte[] pad8LSB(byte[] paramArrayOfByte, short paramShort, int paramInt) {
        byte[] arrayOfByte1 = null;
        if (paramArrayOfByte == null) {
            return (byte[])arrayOfByte1;
        } else {
            int i = (short)(paramInt % 8);
            byte[] arrayOfByte;
            if (i == 0) {
                arrayOfByte = new byte[paramInt + 8];
                System.arraycopy(paramArrayOfByte, paramShort, arrayOfByte, 0, paramInt);
                System.arraycopy(PADDING_8LSB, 0, arrayOfByte, arrayOfByte.length - 8, 8);
                return arrayOfByte;
            } else {
                arrayOfByte = new byte[paramInt + 8 - i];
                System.arraycopy(paramArrayOfByte, paramShort, arrayOfByte, 0, paramInt);
                arrayOfByte[paramInt] = -128;

                for(short j = 1; j < 8 - i; ++j) {
                    arrayOfByte[paramInt + j] = 0;
                }

                return arrayOfByte;
            }
        }
    }

    public static byte[] padBeforeEncrypt(byte[] paramArrayOfByte) {
        if (paramArrayOfByte != null && paramArrayOfByte.length != 0) {
            byte[] arrayOfByte = new byte[2 + paramArrayOfByte.length];
            ByteUtil.shortToByteArray(paramArrayOfByte.length, arrayOfByte, 0);
            System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 2, paramArrayOfByte.length);
            return arrayOfByte.length % 8 == 0 ? arrayOfByte : pad8LSB(arrayOfByte, (short)0, arrayOfByte.length);
        } else {
            return paramArrayOfByte;
        }
    }

    public static byte[] unpadAfterDecrypt(byte[] paramArrayOfByte) {
        if (paramArrayOfByte != null && paramArrayOfByte.length != 0) {
            int i = ByteUtil.byteArrayToShort(paramArrayOfByte, 0);
            byte[] arrayOfByte = new byte[i];
            System.arraycopy(paramArrayOfByte, 2, arrayOfByte, 0, i);
            return arrayOfByte;
        } else {
            return paramArrayOfByte;
        }
    }

    public static int unpadAfterDecrypt(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, short paramShort) {
        if (paramArrayOfByte1 != null && paramArrayOfByte1.length != 0) {
            int i = ByteUtil.byteArrayToShort(paramArrayOfByte1, paramInt);
            System.arraycopy(paramArrayOfByte1, paramInt + 2, paramArrayOfByte2, paramShort, i);
            return i;
        } else {
            return 0;
        }
    }

    public static int encryptDESede_ECB_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws Exception {
        SecretKey localSecretKey = createSecretKey(paramArrayOfByte1);
        Cipher localCipher = Cipher.getInstance("DESede/ECB/NoPadding");
        localCipher.init(1, localSecretKey);
        return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3, paramInt3);
    }

    public static int decryptDESede_ECB_NoPadding(byte[] keyBytes, byte[] input, int intputOffset, int inputLen, byte[] output, int outputOffset) throws Exception {
        SecretKey localSecretKey = createSecretKey(keyBytes);
        Cipher localCipher = Cipher.getInstance("DESede/ECB/NoPadding");
        localCipher.init(2, localSecretKey);
        return localCipher.doFinal(input, intputOffset, inputLen, output, outputOffset);
    }

    public static int encryptDESede_CBC_NoPadding(byte[] keyBytes, byte[] icv, byte[] input, int intputOffset, int inputLen, byte[] output, int outputOffset) throws Exception {
        SecretKey secretKey = createSecretKey(keyBytes);
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(icv);
        cipher.init(1, secretKey, ivParameterSpec);
        return cipher.doFinal(input, intputOffset, inputLen, output, outputOffset);
    }

    public static int decryptDESede_CBC_NoPadding(byte[] keyBytes, byte[] icv, byte[] input, int intputOffset, int inputLen, byte[] output, int outputOffset) throws Exception {
        SecretKey secretKey = createSecretKey(keyBytes);
        Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(icv);
        cipher.init(2, secretKey, ivParameterSpec);
        return cipher.doFinal(input, intputOffset, inputLen, output, outputOffset);
    }

    public static int encryptDES_ECB_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws Exception {
        SecretKey localSecretKey = createSecretKey(paramArrayOfByte1, 0, 8);
        Cipher localCipher = Cipher.getInstance("DES/ECB/NoPadding");
        localCipher.init(1, localSecretKey);
        return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3, paramInt3);
    }

    public static int decryptDES_ECB_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws Exception {
        SecretKey localSecretKey = createSecretKey(paramArrayOfByte1, 0, 8);
        Cipher localCipher = Cipher.getInstance("DES/ECB/NoPadding");
        localCipher.init(2, localSecretKey);
        return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3, paramInt3);
    }

    public static int encryptDES_CBC_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, byte[] paramArrayOfByte4, int paramInt3) throws Exception {
        SecretKey localSecretKey = createSecretKey(paramArrayOfByte1, 0, 8);
        Cipher localCipher = Cipher.getInstance("DES/CBC/NoPadding");
        IvParameterSpec localIvParameterSpec = new IvParameterSpec(paramArrayOfByte2);
        localCipher.init(1, localSecretKey, localIvParameterSpec);
        return localCipher.doFinal(paramArrayOfByte3, paramInt1, paramInt2, paramArrayOfByte4, paramInt3);
    }

    public static int decryptDES_CBC_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt1, int paramInt2, byte[] paramArrayOfByte4, int paramInt3) throws Exception {
        SecretKey localSecretKey = createSecretKey(paramArrayOfByte1, 0, 8);
        Cipher localCipher = Cipher.getInstance("DES/CBC/NoPadding");
        IvParameterSpec localIvParameterSpec = new IvParameterSpec(paramArrayOfByte2);
        localCipher.init(2, localSecretKey, localIvParameterSpec);
        return localCipher.doFinal(paramArrayOfByte3, paramInt1, paramInt2, paramArrayOfByte4, paramInt3);
    }

    public static int encryptAES_CBC_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws Exception {
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1, "AES");
        Cipher localCipher = Cipher.getInstance("AES/CBC/NoPadding");
        localCipher.init(1, localSecretKeySpec);
        return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3, paramInt3);
    }

    public static int decryptAES_CBC_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws Exception {
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1, "AES");
        Cipher localCipher = Cipher.getInstance("AES/CBC/NoPadding");
        localCipher.init(2, localSecretKeySpec);
        return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3, paramInt3);
    }

    public static int encryptAES_ECB_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws Exception {
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1, "AES");
        Cipher localCipher = Cipher.getInstance("AES/ECB/NoPadding");
        localCipher.init(1, localSecretKeySpec);
        return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3, paramInt3);
    }

    public static int decryptAES_ECB_NoPadding(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3, int paramInt3) throws Exception {
        SecretKeySpec localSecretKeySpec = new SecretKeySpec(paramArrayOfByte1, "AES");
        Cipher localCipher = Cipher.getInstance("AES/ECB/NoPadding");
        localCipher.init(2, localSecretKeySpec);
        return localCipher.doFinal(paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3, paramInt3);
    }

    public static short calculateMFPassword(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) throws Exception {
        if (paramArrayOfByte3 != null && paramArrayOfByte3.length >= 8) {
            byte[] arrayOfByte1 = new byte[8];
            int i = 0;
            arrayOfByte1[7] = (byte)((byte)(paramArrayOfByte1[0] << 1) | i);
            arrayOfByte1[6] = (byte)((byte)(paramArrayOfByte1[1] << 1) | i);
            arrayOfByte1[5] = (byte)((byte)(paramArrayOfByte1[2] << 1) | i);
            arrayOfByte1[4] = (byte)((byte)(paramArrayOfByte1[3] << 1) | i);
            arrayOfByte1[3] = (byte)((byte)(paramArrayOfByte1[4] << 1) | i);
            arrayOfByte1[2] = (byte)((byte)(paramArrayOfByte1[5] << 1) | i);
            arrayOfByte1[1] = (byte)(64 & paramArrayOfByte1[0] >> 1 | 32 & paramArrayOfByte1[1] >> 2 | 16 & paramArrayOfByte1[2] >> 3 | 8 & paramArrayOfByte1[3] >> 4 | 4 & paramArrayOfByte1[4] >> 5 | 2 & paramArrayOfByte1[5] >> 6 | i);
            arrayOfByte1[0] = (byte)i;
            byte[] arrayOfByte2 = new byte[]{(byte)((byte)(paramArrayOfByte2[5] << 1) | i), (byte)((byte)(paramArrayOfByte2[4] << 1) | i), (byte)((byte)(paramArrayOfByte2[3] << 1) | i), (byte)((byte)(paramArrayOfByte2[2] << 1) | i), (byte)((byte)(paramArrayOfByte2[1] << 1) | i), (byte)((byte)(paramArrayOfByte2[0] << 1) | i), (byte)(64 & paramArrayOfByte2[5] >> 1 | 32 & paramArrayOfByte2[4] >> 2 | 16 & paramArrayOfByte2[3] >> 3 | 8 & paramArrayOfByte2[2] >> 4 | 4 & paramArrayOfByte2[1] >> 5 | 2 & paramArrayOfByte2[0] >> 6 | i), (byte)i};
            byte[] arrayOfByte3 = new byte[16];
            System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, 8);
            System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 8, 8);
            byte[] arrayOfByte4 = new byte[8];
            encryptDESede_ECB_NoPadding(arrayOfByte3, ZERO_8BYTES, 0, 8, arrayOfByte4, 0);
            paramArrayOfByte3[7] = arrayOfByte4[0];
            paramArrayOfByte3[6] = arrayOfByte4[1];
            paramArrayOfByte3[5] = arrayOfByte4[2];
            paramArrayOfByte3[4] = arrayOfByte4[3];
            paramArrayOfByte3[3] = arrayOfByte4[4];
            paramArrayOfByte3[2] = arrayOfByte4[5];
            paramArrayOfByte3[1] = arrayOfByte4[6];
            paramArrayOfByte3[0] = arrayOfByte4[7];
            return (short)paramArrayOfByte3.length;
        } else {
            return 0;
        }
    }

    public static void main(String[] paramArrayOfString) {
        byte[] arrayOfByte1 = new byte[]{-1, -1, -1, -1, -1, -1};
        byte[] arrayOfByte2 = new byte[]{-1, -1, -1, -1, -1, -1};
        byte[] arrayOfByte3 = new byte[8];

        try {
            calculateMFPassword(arrayOfByte1, arrayOfByte2, arrayOfByte3);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        System.out.println("password: " + ByteUtil.byteArrayToHex(arrayOfByte3));
    }
}
