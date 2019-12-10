package com.zz.base;

import com.zz.upms.base.utils.CustomApacheHttpClient;
import org.junit.Test;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-12-10 10:09
 * ************************************
 */
public class TestHttpClient {
    private String gxUrl = "https://mail.gxecard.com:8440/localAreaNetworkServer/";

    private String clientKsPath = "E:\\key\\gx-test-key\\keystore.jks";
    private String ksPass = "2lgAVlzWZWw=";
    private String keyPass = "2lgAVlzWZWw=";
    private String clientKsTrustPath = "E:\\key\\gx-test-key\\truststore.jks";
    private String ksTrustPass = "2lgAVlzWZWw=";

    @Test
    public void testHttpsGet() throws Exception {
        CustomApacheHttpClient client = new CustomApacheHttpClient(clientKsPath, ksPass, keyPass, clientKsTrustPath, ksTrustPass, 5000);

        String result = client.doGet(gxUrl, "utf-8");
        System.out.println(result);

        Thread.sleep(10000);

        result = client.doGet(gxUrl, "utf-8");
        System.out.println(result);
    }
}
