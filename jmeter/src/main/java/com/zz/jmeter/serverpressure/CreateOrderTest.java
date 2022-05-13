package com.zz.jmeter.serverpressure;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zz.jmeter.common.Constants;
import com.zz.jmeter.exception.BizException;
import com.zz.jmeter.utils.CustomApacheHttpClient;
import com.zz.jmeter.utils.RSASignatureUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-05-10 15:34
 * ************************************
 */
public class CreateOrderTest implements Serializable {
    private static String cplc = "479007644701F35606009205063056400066481000000051000004501D257A1D80010000000000354647";
    //private static String address = "https://172.16.80.103:9088/sptsm/dispacher";
    private static String address = "http://172.16.80.134:8088/sptsm/dispacher";
    private static List<String> personalized = Arrays.asList(
            "{\"apduCount\":2,\"apduList\":[{\"apduContent\":\"6F16840CA0000001515350414F50504FA50673009F6501FF9000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"CF0A5646430630564000660B9000\",\"apduNo\":\"2\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}",
            "{\"apduCount\":1,\"apduList\":[{\"apduContent\":\"5646430630564000660B22020004329411DFD1EACEB0C42E479F61B49000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}",
            "{\"apduCount\":8,\"apduList\":[{\"apduContent\":\"9000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"009000\",\"apduNo\":\"2\",\"apduStatus\":\".*6F70$|.*9000$\"},{\"apduContent\":\"009000\",\"apduNo\":\"3\",\"apduStatus\":\".*6F70$|.*9000$\"},{\"apduContent\":\"009000\",\"apduNo\":\"4\",\"apduStatus\":\".*6F70$|.*9000$\"},{\"apduContent\":\"009000\",\"apduNo\":\"5\",\"apduStatus\":\".*6985$|.*9000$\"},{\"apduContent\":\"009000\",\"apduNo\":\"6\",\"apduStatus\":\".*6985$|.*9000$\"},{\"apduContent\":\"6F15840BA0000000006861696E616EA50673009F6501FF9000\",\"apduNo\":\"7\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"CF0A696E616E0630564000669000\",\"apduNo\":\"8\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}",
            "{\"apduCount\":1,\"apduList\":[{\"apduContent\":\"696E616E063056400066200200008EA86337450DD4CA70B45D68832F9000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}",
            "{\"apduCount\":5,\"apduList\":[{\"apduContent\":\"9000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"2\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"208CC29376B4F03A4E789000\",\"apduNo\":\"3\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"6F0F840DA000000632010106484E594B549000\",\"apduNo\":\"4\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"594B540630564000660B200200002E6007CD30FAAD3B951601FEC1B39000\",\"apduNo\":\"5\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}",
            "{\"apduCount\":34,\"apduList\":[{\"apduContent\":\"9000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"2\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"3\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"4\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"5\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"6\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"7\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"8\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"9\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"10\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"11\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"12\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"13\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"14\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"15\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"16\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"17\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"18\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"19\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"20\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"21\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"22\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"23\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"24\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"25\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"26\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"27\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"28\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"29\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"30\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"31\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"32\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"6F15840BA0000000006861696E616EA50673009F6501FF9000\",\"apduNo\":\"33\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"594B540630564000660B2002000176B7F0FA672D988E0E31260DA0709000\",\"apduNo\":\"34\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}",
            "{\"apduCount\":51,\"apduList\":[{\"apduContent\":\"9000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"009000\",\"apduNo\":\"2\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"3\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"4\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"5\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"6\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"7\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"8\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"9\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"10\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"11\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"12\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"13\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"14\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"15\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"16\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"17\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"18\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"19\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"20\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"21\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"22\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"23\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"24\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"25\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"26\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"27\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"28\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"29\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"30\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"31\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"32\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"33\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"34\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"35\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"36\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"37\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"38\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"39\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"40\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"41\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"42\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"43\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"44\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"45\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"46\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"47\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"48\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"49\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"6F4A8408A000000632010106A53E500A4D4F545F545F434153488701019F38099F7A019F02065F2A025F2D027A689F1101019F120A4D4F545F545F43415348BF0C0A9F4D020B0ADF4D020C0A9000\",\"apduNo\":\"50\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9F360200009000\",\"apduNo\":\"51\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}",
            "{\"apduCount\":19,\"apduList\":[{\"apduContent\":\"9000\",\"apduNo\":\"1\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"2\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"3\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"4\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"5\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"6\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"7\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"8\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"9\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"10\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"11\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"12\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"13\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"14\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"15\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"16\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"17\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"18\",\"apduStatus\":\".*9000$\"},{\"apduContent\":\"9000\",\"apduNo\":\"19\",\"apduStatus\":\".*9000$\"}],\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"get.apdu\",\"cplc\":\"%s\",\"issuerid\":\"vfc_hainan_union\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"transactionid\":\"%s\"}"
    );

    //@Override
    /*public SampleResult runTest(JavaSamplerContext context) {
        return null;
    }*/
    private static Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        /*String createOrder = "{\"amountEnroll\":\"1.00\",\"amountRecharge\":\"10.00\",\"appid\":\"com.finshell.wallet\",\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"changeType\":\"2\",\"command\":\"create.order\",\"cplc\":\"479007644701F35606009205063056400066481000000051000004501D257A1D80010000000000354647\",\"currency\":\"CNY\",\"deviceModel\":\"RMX2142\",\"imei\":\"unknown\",\"issuerid\":\"vfc_hainan_union\",\"payType\":\"hwpay\",\"priceEnroll\":\"1.00\",\"priceRecharge\":\"10.00\",\"seChipManuFacturer\":\"01\",\"serviceCharge\":\"0.00\",\"sign\":\"hXOT4eymbXocypQRSDdPBVFFsHnlUBNBUa/R3TbGWoK+DKYQj1Je/u7/wrnHWGpWI2Of9C0K3u96Tg71P1AXH/tM86EuSU1FTjGxK4/58v1Fpfs6/gdK8XXZiGXBTxvv7cfZD8yb6RcKF2rxJ1jsfH7P5RxSDAvjYg0IZoLj44zJKFP5xsFb1Aim/ed6NHRIHC/1aMqzrupq4AvB2+hnNJZ+zVyGz0lmE1q2h/rIUw8u1hLfVLORmddtK+wT5JvchHO8FqXu56/e4J23sszdgap/RGt7xfVrXBwTHurWwCYkmcCJQIOVCuF6CgTq9JQDFGBj+5IHB7aCZ0kd2/FnEQ==\",\"signType\":\"SHA256WithRSA\",\"spid\":\"1542810138\",\"userid\":\"2000072078\"}";
        JsonObject jsonObject = new Gson().fromJson(createOrder, JsonObject.class);
        createOrder = jsonObject.toString();
        JsonObject resp = getResp(createOrder);
        String orderNo = resp.get("paymentOrder").getAsJsonObject().get("requestId").getAsString();
        System.out.println("orderNo: " + orderNo);*/

        String transactionid = personalize("20220512000000024969617026138112");
        System.out.println("transactionid: " + transactionid);
        getApdu(transactionid, personalized);
    }

    private static String personalize(String orderNo) throws Exception {
        String reqJson = "{\"appletAid\":\"A000000632010105484E594B54\",\"caller\":\"OPPOWallet\",\"command\":\"personalized\",\"cplc\":\"%s\",\"deviceModel\":\"RMX2142\",\"issuerid\":\"vfc_hainan_union\",\"orderNo\":\"%s\",\"seChipManuFacturer\":\"01\",\"spid\":\"1542810138\",\"ssdAid\":\"A0000001515350414F50504F\",\"ssdType\":\"AM\"}";
        reqJson = String.format(reqJson, cplc, orderNo);

        CustomApacheHttpClient client = new CustomApacheHttpClient(10000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.VFC_SIGNATURE, "1");//内部调用标识
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

    private static void getApdu(String transactionid, List<String> apduList) throws Exception {
        for (String req : apduList) {
            String reqJson = String.format(req, cplc, transactionid);

            CustomApacheHttpClient client = new CustomApacheHttpClient(10000);

            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.VFC_SIGNATURE, "1");//内部调用标识
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


    private static JsonObject getResp(String requestJson) throws Exception{
        JsonObject jsonObject = gson.fromJson(requestJson, JsonObject.class);

        System.out.println(Thread.currentThread().getId() + "  请求: " + jsonObject.toString());
        CustomApacheHttpClient client = new CustomApacheHttpClient(10000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.VFC_SIGNATURE, "1");//内部调用标识
        headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
        headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(jsonObject.toString(), Constants.priKey,
                "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

        String respStr = client.doPost(address, jsonObject.toString(), "utf-8", headers);

        System.out.println(Thread.currentThread().getId() + "  响应: " + respStr);

        jsonObject = gson.fromJson(respStr, JsonObject.class);
        if(!"0".equals(jsonObject.get("returnCode").getAsString())){
            throw new BizException("应答码异常");
        }

        return jsonObject;
    }
}
