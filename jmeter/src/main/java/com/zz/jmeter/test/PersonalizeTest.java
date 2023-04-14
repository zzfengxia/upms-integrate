/*
package com.zz.jmeter.test;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zz.jmeter.common.Constants;
import com.zz.jmeter.exception.BizException;
import com.zz.jmeter.utils.CustomApacheHttpClient;
import com.zz.jmeter.utils.HexUtil;
import com.zz.jmeter.utils.RSASignatureUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

*/
/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2022-05-16 15:26
 * ************************************
 *//*

public class PersonalizeTest extends AbstractJavaSamplerClient implements Serializable {
    private static Gson gson = new Gson();
    private static Set<String> randomPool = Sets.newConcurrentHashSet();
    private static List<String> personalized = Arrays.asList(
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":2,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"6F44840CA00000000353504200080817A534732E06072A864886FC6B01600B06092A864886FC6B020203630906072A864886FC6B03640B06092A864886FC6B0402559F6501FF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"2\",\"apduContent\":\"%s\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":1,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"5646430907376504620B2002003C29B17787DAB9D6F4B138FD15DE969000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":10,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"2\",\"apduContent\":\"1D0814D2B4193618EB140200121000112233445566778899AABBCCDDEEFF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"3\",\"apduContent\":\"1D08D1AB6C7EF931C9AF0200131000112233445566778899AABBCCDDEEFF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"4\",\"apduContent\":\"1D08DD6F48B17F0B78A60200141000112233445566778899AABBCCDDEEFF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"5\",\"apduContent\":\"1D081DEF2AF98F7414200200151000112233445566778899AABBCCDDEEFF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"6\",\"apduContent\":\"1D080BFE2DC6DC4EE9500200161000112233445566778899AABBCCDDEEFF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"7\",\"apduContent\":\"1D08CFE08A3B7F2E3BE50200171000112233445566778899AABBCCDDEEFF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"8\",\"apduContent\":\"1D0809C8D3B2772387110200181000112233445566778899AABBCCDDEEFF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"9\",\"apduContent\":\"6F43840BA000000071696E6764616FA534732E06072A864886FC6B01600B06092A864886FC6B020203630906072A864886FC6B03640B06092A864886FC6B0402559F6501FF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"10\",\"apduContent\":\"CF0A6764616F0907376504629000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":1,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"6764616F090737650462200200001E2F48A6E1D0B0BFB1E9B1E95D579000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":3,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"2\",\"apduContent\":\"6F0F840DA00000063201010651445144549000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"3\",\"apduContent\":\"6764616F090737650462200200016A11AC1E9F69CA1612C40FB073349000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":34,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"2\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"3\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"4\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"5\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"6\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"7\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"8\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"9\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"10\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"11\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"12\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"13\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"14\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"15\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"16\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"17\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"18\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"19\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"20\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"21\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"22\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"23\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"24\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"25\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"26\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"27\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"28\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"29\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"30\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"31\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"32\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"33\",\"apduContent\":\"6F43840BA000000071696E6764616FA534732E06072A864886FC6B01600B06092A864886FC6B020203630906072A864886FC6B03640B06092A864886FC6B0402559F6501FF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"34\",\"apduContent\":\"6764616F09073765046220020002216BBA0E2F7920B00C48E831A83B9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":25,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"2\",\"apduContent\":\"009000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"3\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"4\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"5\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"6\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"7\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"8\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"9\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"10\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"11\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"12\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"13\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"14\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"15\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"16\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"17\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"18\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"19\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"20\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"21\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"22\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"23\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"24\",\"apduContent\":\"6F43840BA000000071696E6764616FA534732E06072A864886FC6B01600B06092A864886FC6B020203630906072A864886FC6B03640B06092A864886FC6B0402559F6501FF9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"25\",\"apduContent\":\"6764616F090737650462200200036B1F183C480DC6D37E2B7ECE29A79000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":96,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"2\",\"apduContent\":\"009000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"3\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"4\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"5\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"6\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"7\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"8\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"9\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"10\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"11\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"12\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"13\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"14\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"15\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"16\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"17\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"18\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"19\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"20\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"21\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"22\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"23\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"24\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"25\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"26\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"27\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"28\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"29\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"30\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"31\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"32\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"33\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"34\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"35\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"36\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"37\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"38\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"39\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"40\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"41\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"42\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"43\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"44\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"45\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"46\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"47\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"48\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"49\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"50\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"51\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"52\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"53\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"54\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"55\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"56\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"57\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"58\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"59\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"60\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"61\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"62\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"63\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"64\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"65\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"66\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"67\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"68\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"69\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"70\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"71\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"72\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"73\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"74\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"75\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"76\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"77\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"78\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"79\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"80\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"81\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"82\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"83\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"84\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"85\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"86\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"87\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"88\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"89\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"90\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"91\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"92\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"93\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"94\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"95\",\"apduContent\":\"6F518408A000000632010106A545500B50424F43204372656469748701019F380F9F1A029F7A019F02065F2A029F4E145F2D027A689F1101019F120A4D4F545F545F43415348BF0C0A9F4D020B0ADF4D020C0A9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"96\",\"apduContent\":\"9F360200009000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}",
            "{\"command\":\"get.apdu\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"transactionid\":\"%s\",\"apduCount\":19,\"reserved\":null,\"apduList\":[{\"apduNo\":\"1\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"2\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"3\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"4\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"5\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"6\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"7\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"8\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"9\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"10\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"11\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"12\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"13\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"14\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"15\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"16\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"17\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"18\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null},{\"apduNo\":\"19\",\"apduContent\":\"9000\",\"apduStatus\":\"9000\",\"command\":null,\"checker\":null}]}"
    );
    private static String address = "http://172.16.80.132:8087/sptsm/dispacher";

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.setResponseCode("200");
        result.setDataType(SampleResult.TEXT);
        result.setSuccessful(true);
        try {
            String cplc = genCplc();
            String orderNo = createOrder(cplc);

            String transactionid = personalize(orderNo, cplc);
            getApdu(transactionid, personalized, cplc);
        } catch (Exception e) {
            result.setResponseCode("500");
            result.setSuccessful(false);
            return result;
        }
        return result;
    }

    private String createOrder(String cplc) throws Exception {
        String createOrder = "{\"command\":\"create.order\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"eventid\":null,\"userid\":\"08605321c3b95b08\",\"phoneNumber\":null,\"cardNo\":null,\"payType\":\"hwpay\",\"changeType\":\"2\",\"amountEnroll\":\"0.00\",\"amountRecharge\":\"0.01\",\"priceEnroll\":\"0.00\",\"priceRecharge\":\"0.01\",\"activityId\":null,\"amountCardMove\":null,\"priceCardMove\":null,\"currency\":\"CNY\",\"reserved\":null,\"appCode\":null,\"appid\":\"com.huawei.wallet\",\"imei\":\"08605321e2a16455bc3e\",\"upDiscountCode\":null,\"entrustRequestId\":null,\"entrustType\":null,\"isEntrustOrder\":null,\"userName\":null,\"wechatAppid\":null,\"orderDesc\":null,\"customerInfo\":null,\"couponInfo\":null,\"userIp\":null,\"pageBackUrl\":null,\"cardenrollDiscountAmount\":\"0.00\",\"rechargeDiscountAmount\":\"0.00\",\"couponDiscountAmount\":null,\"payAmount\":null,\"cardenrollActivityCode\":null,\"rechargeActivityCode\":null,\"couponActivityCode\":null,\"payChannelActivityCode\":null,\"extendInfo\":null,\"subType\":null}";
        createOrder = String.format(createOrder, cplc);

        CustomApacheHttpClient client = new CustomApacheHttpClient(5000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.VFC_SIGNATURE, "1");//内部调用标识
        headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
        headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(createOrder, Constants.priKey,
                "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

        String respStr = client.doPost(address, createOrder, "utf-8", headers);

        JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
        if(!"0".equals(jsonObject.get("returnCode").getAsString())){
            throw new BizException("应答码异常");
        }
        String orderNo = jsonObject.get("paymentOrder").getAsJsonObject().get("requestId").getAsString();

        return orderNo;
    }

    private String personalize(String orderNo, String cplc) throws Exception {
        String reqJson = "{\"command\":\"personalized\",\"caller\":\"HuaweiWallet\",\"appletAid\":\"A00000000386980700\",\"cplc\":\"%s\",\"deviceModel\":\"NCO-AL00\",\"issuerid\":\"t_vfc_qingdao\",\"seChipManuFacturer\":\"01\",\"spid\":\"890086000000001154\",\"clientVersion\":null,\"orderNo\":\"%s\",\"phoneNumber\":\"18888888888\",\"userid\":\"08605321c3b95b08\",\"basebandVersion\":null,\"systemType\":null,\"systemVersion\":\"30\",\"seCosVersion\":null,\"ssdAid\":\"A00000000353504200080817\",\"ssdType\":\"DM\",\"imei\":\"08605321e2a16455bc3e\",\"phoneManufacturer\":null,\"reserved\":\"{}\",\"uid\":null}";
        reqJson = String.format(reqJson, cplc, orderNo);

        CustomApacheHttpClient client = new CustomApacheHttpClient(5000);

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.VFC_SIGNATURE, "1");//内部调用标识
        headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
        headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(reqJson, Constants.priKey, "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

        String respStr = client.doPost(address, reqJson, "utf-8", headers);

        JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
        if(!"0".equals(jsonObject.get("returnCode").getAsString())){
            throw new BizException("应答码异常");
        }

        return jsonObject.get("transactionid").getAsString();
    }

    private void getApdu(String transactionid, List<String> apduList, String cplc) throws Exception {
        for (int i = 0; i < apduList.size(); i++) {
            String reqJson;

            if(i == 0) {
                String keyData = genKeyData(cplc);
                reqJson = String.format(apduList.get(i), cplc, transactionid, keyData);
            } else {
                reqJson = String.format(apduList.get(i), cplc, transactionid);
            }
            CustomApacheHttpClient client = new CustomApacheHttpClient(5000);

            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.VFC_SIGNATURE, "1");//内部调用标识
            headers.put(Constants.SIGNATURE_TYPE, RSASignatureUtil.SIGN_ALGORITHMS_SHA256);
            headers.put(Constants.SIGNATURE_VALUE, RSASignatureUtil.sign(reqJson, Constants.priKey, "UTF-8", RSASignatureUtil.SIGN_ALGORITHMS_SHA256));

            String respStr = client.doPost(address, reqJson, "utf-8", headers);


            JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
            if(!"0".equals(jsonObject.get("returnCode").getAsString())) {
                throw new BizException("应答码异常");
            }
        }
    }

    private String genCplc() {
        String cplc = "007F0001867F203100012041090737650462000900000009000000092064569300000000000056930000";
        String randomUid;
        do{
            randomUid = HexUtil.encodeHexStr(RandomUtils.nextBytes(8));
        } while (!randomPool.add(randomUid));
        return  cplc.replace("2041090737650462", randomUid);
    }

    private String genKeyData(String cplc) {
        String keyData = "CF0A5646430907376504620B9000";
        return keyData.replace("090737650462", cplc.substring(24, 36));
    }
}
*/
