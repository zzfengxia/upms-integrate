package com.zz.jmeter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.zz.jmeter.utils.CustomApacheHttpClientForKeep;
import com.zz.jmeter.utils.DateUtil;
import com.zz.jmeter.utils.HexUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-12-31 11:18
 * ************************************
 */
public class LongConnectTest extends AbstractJavaSamplerClient implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(LongConnectTest.class);

    private CustomApacheHttpClientForKeep client;
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
        String url = context.getParameter("path");
        String encoding = context.getParameter("encoding");
        int timeout = context.getIntParameter("timeout", 5000);

        client = new CustomApacheHttpClientForKeep(url, timeout, encoding);
        super.setupTest(context);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        Long start = System.currentTimeMillis();
        String method = javaSamplerContext.getParameter("method");
        String reqstr = javaSamplerContext.getParameter("reqstr");
        if(client == null) {
            throw new IllegalArgumentException("client is null");
        }

        String traceId = HexUtil.genTraceId();
        Map<String, String> formParams = ImmutableMap.of("traceId", traceId);
        SampleResult result = new SampleResult();

        String curTime = DateUtil.getCurrentTime24SSS();
        try {
            result.sampleStart();
            if("post".equalsIgnoreCase(method)) {
                client.doPostWithUrlParams(reqstr, formParams);
            } else {
                client.doGet(formParams);
            }

            //请求成功，设置测试结果为成功
            result.setSuccessful(true);
        } catch (Exception e) {
            result.setSuccessful(false);
            logger.error("traceId:" + traceId + ",curTime: " + curTime + ", request error.", e);
        } finally {
            Long end = System.currentTimeMillis();
            result.sampleEnd();
            logger.error("traceId:" + traceId + ", 执行耗时:" + (end - start) + "ms");
        }
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        super.teardownTest(context);
    }
}
