package com.zz.jmeter.utils;

import java.util.UUID;

/**
 * Created by zengzheng on 2016-04-25.
 * 描述：16进制与字节数组的转换 <br/>
 */
public class HexUtil {

    public static final int CONVERT_MODEL_UPPER = 0;
    public static final int CONVERT_MODEL_LOWER = 1;

    /**
     * 将字节数组转换为16进制字符数据
     * @param data
     * @return
     * @throws Exception
     */
    public static char[] encodeHex(byte[] data) throws Exception {
        if(data == null || data.length == 0) {
            throw new Exception("不存在需要转换的数据!");
        }
        int dataLen = data.length;
        char[] enChar = new char[dataLen << 1];
        /**
         * 将十进制数字与0xf0作与运算并右移4位的结果作为16进制的高位并转为16进制字符
         * 与0x0f作与运算后的结果作为16进制的低位并转为16进制字符
         */
        for(int i = 0, j = 0; i < dataLen; i++) {
            enChar[j++] = Character.forDigit((0xf0 & data[i]) >>> 4, 16);
            enChar[j++] = Character.forDigit(0x0f & data[i], 16);
        }

        return enChar;
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param data
     * @param type 大写或小写
     * @return
     * @throws Exception
     */
    public static String encodeHexStr(byte[] data, int type) throws Exception {
        type = type == -1 ? CONVERT_MODEL_UPPER : type;
        String result = null;
        if(type == CONVERT_MODEL_UPPER) {
            result = new String(encodeHex(data)).toUpperCase();
        }else {
            result = new String(encodeHex(data)).toLowerCase();
        }

        return result;
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param data
     * @return
     * @throws Exception
     */
    public static String encodeHexStr(byte[] data) {
        try {
            return encodeHexStr(data, -1);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 16进制字符串解码为字节数组
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] hexToByteArray(char[] data) throws Exception {
        if(data == null || data.length == 0) {
            throw new Exception("不存在需要解析的数据!");
        }
        int dataLen = data.length;
        if((dataLen & 0x01) != 0) {
            throw new Exception("需要解析的字符长度应为偶数!");
        }
        byte[] deByte = new byte[dataLen >> 1];
        for(int i = 0, j = 0; j < dataLen; i++) {
            int hexHigh = Character.digit(data[j++], 16) << 4;
            int hexLow = Character.digit(data[j++], 16);
            deByte[i] = (byte) ((hexHigh | hexLow) & 0xff);
        }
        return deByte;
    }

    /**
     * 16进制字符串解码为字节数组
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] hexToByteArray(String data) {
        String[] tempData = data.split(" ");
        if(tempData.length > 1) {
            data = StringUtil.arrayToString(tempData, null);
        }
        try {
            return hexToByteArray(data.toCharArray());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 16进制字符串转换为字节
     * @param data
     * @return
     * @throws Exception
     */
    public static byte hexToByte(String data) throws Exception {
        if(data == null || data.length() != 2) {
            throw new Exception("Hex String is invalid.");
        }
        return hexToByteArray(data)[0];
    }

    /**
     * 16进制字符串解码为正常字符串数据
     * @param data
     * @return
     * @throws Exception
     */
    public static String decodeHexStr(char[] data) throws Exception {
        return new String(hexToByteArray(data));
    }

    /**
     * 16进制字符串解码为正常字符串数据
     * @param data
     * @return
     * @throws Exception
     */
    public static String decodeHexStr(String data) throws Exception {
        return new String(hexToByteArray(data));
    }

    public static String genTraceId() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
