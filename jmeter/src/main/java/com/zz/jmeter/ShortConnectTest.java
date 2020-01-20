package com.zz.jmeter;

import com.google.common.collect.ImmutableMap;
import com.zz.jmeter.utils.CustomApacheHttpClient;
import com.zz.jmeter.utils.DateUtil;
import com.zz.jmeter.utils.HexUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-12-31 11:18
 * ************************************
 */
public class ShortConnectTest extends AbstractJavaSamplerClient implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(ShortConnectTest.class);
    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("path", "");
        params.addArgument("method", "");
        params.addArgument("encoding", "");
        params.addArgument("reqstr", "");
        params.addArgument("timeout", "");

        return params;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {

        super.setupTest(context);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        String url = javaSamplerContext.getParameter("path");
        String encoding = javaSamplerContext.getParameter("encoding");
        int timeout = javaSamplerContext.getIntParameter("timeout", 5000);
        String method = javaSamplerContext.getParameter("method");
        String reqstr = javaSamplerContext.getParameter("reqstr");

        String traceId = HexUtil.genTraceId();
        Map<String, String> formParams = ImmutableMap.of("traceId", traceId);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keys = formParams.keySet();
        for(String key : keys) {
            nvps.add(new BasicNameValuePair(key, formParams.get(key)));
        }
        String paramsStr = URLEncodedUtils.format(nvps, "utf-8");
        url = url + "?" + paramsStr;

        Long start = System.currentTimeMillis();
        CustomApacheHttpClient client = new CustomApacheHttpClient(timeout);

        SampleResult result = new SampleResult();
        String curTime = DateUtil.getCurrentTime24SSS();
        try {
            result.sampleStart();
            if("post".equalsIgnoreCase(method)) {
                String res = client.doPost(url, reqstr, encoding);
            }else {
                client.doGet(url, encoding);
            }

            //请求成功，设置测试结果为成功
            result.setSuccessful(true);
        } catch (Exception e) {
            result.setSuccessful(false);
            logger.error("traceId:" + traceId + ",curTime: " + curTime + ", request error.", e);
        } finally {
            Long end = System.currentTimeMillis();
            logger.error("traceId:" + traceId + ", 执行耗时:" + (end - start) + "ms");
            result.sampleEnd();
        }
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        super.teardownTest(context);
    }
}
