package com.zz.jmeter.serverpressure;

import com.zz.jmeter.utils.ByteUtil;
import com.zz.jmeter.utils.MasterKeyHelper;
import com.zz.jmeter.utils.SecurityUtil;

public class MockESEAPDUResp {
    private static final byte[] ISD_KEY_VALUE = new byte[]{(byte) 0x40, (byte) 0x41, (byte) 0x42,
            (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
            (byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4A,
            (byte) 0x4B, (byte) 0x4C, (byte) 0x4D, (byte) 0x4E, (byte) 0x4F};
    public final static byte[] CONST_SESSSION_ENC = new byte[]{01, (byte) 0x82};
    public final static byte[] SESSION_KEY_PADDING = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public final static byte[] PAD_80 = { (byte) 0x80, 0, 0, 0, 0, 0, 0, 0 };
    public final static byte[] ICV_ZERO = { 0, 0, 0, 0, 0, 0, 0, 0 };
    private static boolean isVFCKeyData = false;
    private static boolean isYKTKeyData = false;
    private static final String SSD_KEY = "505152535455565758595a5b5c5d5e5f";

    // return 30 bytes
    public static byte[] generateInitUpdateResponse(byte[] initUpdateAPDU, int offset, byte[] realKeydata, boolean isReplaceKeyData) throws Exception {
        byte[] initUpdateResponse = new byte[30];

        byte[] hostChallage = new byte[8];
        // from 5 to 13
        System.arraycopy(initUpdateAPDU, 5, hostChallage, 0, 8);

        byte[] keyDirveData = new byte[10];
        // 因为realKeydata带了CF0A2个字节，所以去掉这2个字节并截取10个字节
        if (realKeydata != null && realKeydata.length >= 12) {
            System.arraycopy(realKeydata, 2, keyDirveData, 0, 10);
        }
        System.arraycopy(keyDirveData, 0, initUpdateResponse, offset, 10);
        offset += 10;
        byte[] keyInformation = new byte[2];
        keyInformation[1] = 0x02;
        System.arraycopy(keyInformation, 0, initUpdateResponse, offset, 2);
        offset += 2;

        byte[] seqCount = new byte[2];
        seqCount[1] = 1;
        System.arraycopy(seqCount, 0, initUpdateResponse, offset, 2);
        offset += 2;

        byte[] cardChallenge = new byte[6];
        System.arraycopy(cardChallenge, 0, initUpdateResponse, offset, 6);
        offset += 6;

        // 初始密钥为40~4f
        byte[] dkey = ISD_KEY_VALUE;
        // 如果是替换密钥的时候，要用50~5f，所以替换密钥只能是50~5F的测试密钥，否则没法测试
        if(isIsVFCKeyData() || isIsYKTKeyData() || isReplaceKeyData) {
            dkey = ByteUtil.hexToByteArray(SSD_KEY);
        }

        dkey = MasterKeyHelper.getDerivedKeySet(ByteUtil.byteArrayToHex(keyDirveData), ByteUtil.byteArrayToHex(dkey))[0];

        byte[] encSessionKey = createSessionKey(dkey, CONST_SESSSION_ENC,
                seqCount);
        byte[] cardCryporgram = generateAuthCryptogram(encSessionKey,
                hostChallage, seqCount, cardChallenge);
        System.arraycopy(cardCryporgram, 0, initUpdateResponse, offset, 8);
        offset += 8;

        initUpdateResponse[offset] = (byte) 0x90;
        offset++;
        initUpdateResponse[offset] = (byte) 0x00;
        return initUpdateResponse;
    }

    public static boolean isIsVFCKeyData() {
        return isVFCKeyData;
    }
    public static boolean isIsYKTKeyData() {
        return isYKTKeyData;
    }

    private static byte[] generateAuthCryptogram(byte[] encSessionKey,
                                                 byte[] hostChallenge, byte[] intitalSequenceCounter,
                                                 byte[] cardChallenge) throws Exception {
        byte[] data = new byte[24];
        short offset = 0;
        System.arraycopy(hostChallenge, 0, data, offset, 8);
        offset += 8;
        if (intitalSequenceCounter != null && intitalSequenceCounter.length > 0) {
            System.arraycopy(intitalSequenceCounter, 0, data, offset, 2);
            offset += intitalSequenceCounter.length;
        }
        System.arraycopy(cardChallenge, 0, data, offset, cardChallenge.length);
        offset += cardChallenge.length;
        System.arraycopy(PAD_80, 0, data, offset, 8);

        byte[] result = new byte[24];
        SecurityUtil.encryptDESede_CBC_NoPadding(encSessionKey, ICV_ZERO, data,
                (short) 0, 24, result, (short) 0);

        byte[] cryptgram = new byte[8];

        System.arraycopy(result, 16, cryptgram, 0, 8);

        return cryptgram;
    }

    private static byte[] createSessionKey(byte[] derivedKey, byte[] constant,
                                           byte[] intitalSequenceCounter) throws Exception {
        byte[] data = new byte[16];
        short offset = 0;
        System.arraycopy(constant, 0, data, offset, 2);
        offset += 2;
        System.arraycopy(intitalSequenceCounter, 0, data, offset, 2);
        offset += 2;
        System.arraycopy(SESSION_KEY_PADDING, 0, data, offset, 12);

        byte[] sessionKey = new byte[16];
        SecurityUtil.encryptDESede_CBC_NoPadding(derivedKey, ICV_ZERO, data, 0,
                16, sessionKey, 0);
        return sessionKey;
    }
}
