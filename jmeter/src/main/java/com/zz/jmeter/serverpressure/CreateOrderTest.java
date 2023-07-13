package com.zz.jmeter.serverpressure;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zz.jmeter.common.Constants;
import com.zz.jmeter.exception.BizException;
import com.zz.jmeter.utils.CustomApacheHttpClient;
import com.zz.jmeter.utils.HexUtil;
import com.zz.jmeter.utils.RSASignatureUtil;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-05-10 15:34
 * ************************************
 */
public class CreateOrderTest implements Serializable {
    // private static String cplc = "007F0001867F20310001 2041090737650462 000900000009000000092064569300000000000056930000";
    private static Set<String> randomPool = Sets.newConcurrentHashSet();

    private static String address = "http://172.16.80.106:9087/sp/dispacher";
    private static List<String> personalized = Arrays.asList(
            );

    private static List<String> loadCap = Arrays.asList(
            );
    //@Override
    /*public SampleResult runTest(JavaSamplerContext context) {
        return null;
    }*/
    private static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        String cplc = genCplc();
        String orderNo = createOrder(cplc);

        String transactionid = personalize(orderNo, cplc);
        System.out.println("transactionid: " + transactionid);
        //getApdu(transactionid, personalized, cplc);
        /*queryOrder("007F0001867F20310001CE853E63B580404C000900000009000000092064569300000000000056930000");*/

    }

    private static String genCplc() {
        String cplc = "007F0001867F203100012041090737650462000900000009000000092064569300000000000056930000";
        String randomUid;
        do{
            randomUid = HexUtil.encodeHexStr(RandomUtils.nextBytes(8));
        } while (!randomPool.add(randomUid));
        return  cplc.replace("2041090737650462", randomUid);
    }

    private static String genKeyData(String cplc) {
        String keyData = "CF0A5646430907376504620B9000";
        return keyData.replace("090737650462", cplc.substring(24, 36));
    }

    private static String load(String orderNo, String cplc) throws Exception {
        String reqJson = "";
        reqJson = String.format(reqJson, cplc, orderNo);

        CustomApacheHttpClient client = new CustomApacheHttpClient(5000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.SP_SIGNATURE, "1");//内部调用标识
        headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
        headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(reqJson, Constants.priKey, "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

        String respStr = client.doPost(address, reqJson, "utf-8", headers);

        JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
        if(!"0".equals(jsonObject.get("returnCode").getAsString())){
            throw new BizException("应答码异常");
        }

        return jsonObject.get("transactionid").getAsString();
    }

    private static String personalize(String orderNo, String cplc) throws Exception {
        String reqJson = "";
        reqJson = String.format(reqJson, cplc, orderNo);

        CustomApacheHttpClient client = new CustomApacheHttpClient(10000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.SP_SIGNATURE, "1");//内部调用标识
        headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
        headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(reqJson, Constants.priKey, "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

        String respStr = client.doPost(address, reqJson, "utf-8", headers);

        System.out.println("响应: " + respStr);

        JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
        if(!"0".equals(jsonObject.get("returnCode").getAsString())){
            throw new BizException("应答码异常");
        }

        return jsonObject.get("transactionid").getAsString();
    }

    private static void getApdu(String transactionid, List<String> apduList, String cplc) throws Exception {
        for (int i = 0; i < apduList.size(); i++) {
            String reqJson;

            if(i == 0) {
                // "CF0A5646430907376504620B9000"
                String keyData = genKeyData(cplc);
                reqJson = String.format(apduList.get(i), cplc, transactionid, keyData);
            } else {
                reqJson = String.format(apduList.get(i), cplc, transactionid);
            }
            CustomApacheHttpClient client = new CustomApacheHttpClient(10000);

            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.SP_SIGNATURE, "1");//内部调用标识
            headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
            headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(reqJson, Constants.priKey, "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

            String respStr = client.doPost(address, reqJson, "utf-8", headers);

            System.out.println("响应: " + respStr);

            JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
            if(!"0".equals(jsonObject.get("returnCode").getAsString())) {
                throw new BizException("应答码异常");
            }
        }
    }


    private static String createOrder(String cplc) throws Exception {
        String createOrder = "";
        createOrder = String.format(createOrder, cplc);

        CustomApacheHttpClient client = new CustomApacheHttpClient(10000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.SP_SIGNATURE, "1");//内部调用标识
        headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
        headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(createOrder, Constants.priKey,
                "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

        String respStr = client.doPost(address, createOrder, "utf-8", headers);

        System.out.println(Thread.currentThread().getId() + "  响应: " + respStr);

        JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
        if(!"0".equals(jsonObject.get("returnCode").getAsString())){
            throw new BizException("应答码异常");
        }
        String orderNo = jsonObject.get("paymentOrder").getAsJsonObject().get("requestId").getAsString();
        System.out.println("orderNo: " + orderNo);

        return orderNo;
    }

    private static void queryOrder(String cplc) throws Exception {
        String json = "";
        json = String.format(json, cplc);

        CustomApacheHttpClient client = new CustomApacheHttpClient(10000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.SP_SIGNATURE, "1");//内部调用标识
        headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
        headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(json, Constants.priKey,
                "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

        String respStr = client.doPost(address, json, "utf-8", headers);

        System.out.println(Thread.currentThread().getId() + "  响应: " + respStr);

        JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
        if(!"0".equals(jsonObject.get("returnCode").getAsString())){
            throw new BizException("应答码异常");
        }
    }
}
