package com.zz.jmeter.utils;

import com.google.common.collect.Lists;
import com.zz.jmeter.exception.BizException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Francis.zz on 2017/8/1.
 * 支持http单向认证忽略证书，https 双向认证带证书
 * 双向认证请求的证书密码需要KEY加密
 * 使用httpclient4.5.x版本
 */
public class CustomApacheHttpClient {
    private final static Logger logger = LoggerFactory.getLogger(CustomApacheHttpClient.class);

    private final static String KEY = "5E986AA875BD34F12E1098DEAC97CA515E986AA875BD34F1";
    private final static String KEY_STORE_TYPE_JKS = "jks";         // keystore，jks证书
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";		// .p12后缀的证书
    // 超时重试次数
    private static final int TIMEOUT_RETRY_COUNT = 3;

    private String trustPath ;
    private String trustStorePassword ;
    private String keyPath ;
    private String keyStorePassword ;
    private String keyPassword ;
    private CloseableHttpClient client;
    private RequestConfig config;
    private String url;

    public void setTrustStore(String trustPath, String trustStorePassword) {
        this.trustPath = trustPath;
        this.trustStorePassword = DesUtil.desCbcDecode(trustStorePassword, KEY);
    }

    public void setKeyStore(String keyPath, String keyStorePassword, String keyPassword) {
        this.keyPath = keyPath;
        this.keyStorePassword = DesUtil.desCbcDecode(keyStorePassword, KEY);
        this.keyPassword = DesUtil.desCbcDecode(keyPassword, KEY);
    }

    public CustomApacheHttpClient(int timeOut) {
        // 创建请求参数
        if(config == null) {
            config = RequestConfig.custom()
                    .setSocketTimeout(timeOut)
                    .setConnectTimeout(timeOut)
                    .setConnectionRequestTimeout(timeOut)
                    .build();
        }
    }

    /**
     * SSL客户端
     *
     * @param keyPath               密钥库地址
     * @param keyStorePassword      密钥库文件密码
     * @param keyPassword           私钥密码
     * @param trustPath             信任库地址
     * @param trustStorePassword    信任库密码
     * @param timeOut               超时时间
     */
    public CustomApacheHttpClient(String keyPath, String keyStorePassword, String keyPassword, String trustPath, String trustStorePassword, int timeOut) {
        this(timeOut);
        this.trustPath = trustPath;
        this.trustStorePassword = DesUtil.desCbcDecode(trustStorePassword, KEY);
        this.keyPath = keyPath;
        this.keyStorePassword = DesUtil.desCbcDecode(keyStorePassword, KEY);
        this.keyPassword = DesUtil.desCbcDecode(keyPassword, KEY);
    }

    /**
     * SSL忽略信任证书
     *
     * @param keyPath           密钥库地址
     * @param keyStorePassword  密钥库文件密码
     * @param keyPassword       私钥密码
     * @param timeOut           超时时间
     */
    public CustomApacheHttpClient(String keyPath, String keyStorePassword, String keyPassword, int timeOut) {
        this(timeOut);
        this.keyPath = keyPath;
        this.keyStorePassword = DesUtil.desCbcDecode(keyStorePassword, KEY);
        this.keyPassword = DesUtil.desCbcDecode(keyPassword, KEY);
    }

    /**
     * 创建SSLContext
     *
     * @return
     * @throws Exception
     */
    private SSLContext createSSLContext() throws Exception {
        SSLContext sslContext = null;
        if(keyPath != null && keyPassword != null && keyStorePassword != null && trustPath != null && trustStorePassword != null) {
            // https双向认证
            KeyStore truststore = CertUtil.getKeyStoreByCert(trustPath, trustStorePassword);
            KeyStore keyStore = CertUtil.getKeyStoreByCert(keyPath, keyStorePassword);

            sslContext = SSLContexts.custom().loadTrustMaterial(truststore, null)
                    .loadKeyMaterial(keyStore, keyPassword.toCharArray()).build();

        } else if(keyPath != null && keyPassword != null && keyStorePassword != null && trustPath == null && trustStorePassword == null) {
            KeyStore keyStore = CertUtil.getKeyStoreByCert(keyPath, keyStorePassword);
            // https双向认证忽略服务器证书
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(new org.apache.http.conn.ssl.TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).loadKeyMaterial(keyStore, keyPassword.toCharArray()).build();

        } else {
            // https单向认证忽略服务器证书
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(new org.apache.http.conn.ssl.TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
        }

        return sslContext;
    }

    private void createClient(String url) {
        this.url = url;
        try {
            SSLConnectionSocketFactory sslsf = null;
            // 这里填多个协议时会报 handshake_failure 异常；NoopHostnameVerifier不校验域名
            if(url.substring(0, 5).equalsIgnoreCase("https")) {
                sslsf = new SSLConnectionSocketFactory(createSSLContext(),
                        new String[]{"TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            }else {
                // http请求
                sslsf = SSLConnectionSocketFactory.getSocketFactory();
            }

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();
            PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);

            this.client = HttpClients.custom()
                    .setDefaultRequestConfig(this.config) //设置默认的请求参数
                    // 使用短连接
                    .setDefaultHeaders(Lists.newArrayList(
                            new BasicHeader(HttpHeaders.CONNECTION, HTTP.CONN_CLOSE)
                    ))
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(manager)
                    .setConnectionManagerShared(false) //连接池不是共享模式，这个共享是指与其它httpClient是否共享
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))//设置重试次数，默认为3次；当前是禁用掉
                    .build();
        } catch (Exception e) {
            logger.error("create http client failed.", e);
        }
    }

    /**
     * POST/FORM请求，HTTP、HTTPS均可；SSL需要证书，暂只实现双向认证
     *
     * @param url
     * @param formparams
     * @param charset
     * @return
     * @throws Exception
     */
    public String doPost(String url, Map<String, String> formparams, String charset) throws Exception {
        createClient(url);
        checkClient();

        HttpPost httpPost = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keys = formparams.keySet();
        for(String key : keys) {
            nvps.add(new BasicNameValuePair(key, formparams.get(key)));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));

        String recvData = null;
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(config);
            response = client.execute(httpPost) ;
            HttpEntity entity = response.getEntity();
            int respCode = response.getStatusLine().getStatusCode();
            if (respCode != HttpStatus.SC_OK) {
                throw new BizException("HTTP Status Code is " + respCode);
            }

            if (entity != null) {
                recvData = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.info("Response content: " + recvData);
            }
            EntityUtils.consume(entity);
        }finally {
            if(response != null) {
                response.close();
            }
            this.client.close();
        }
        return recvData;
    }

    /**
     * POST，application/json请求，HTTP、HTTPS均可；SSL需要证书，暂只实现双向认证
     *
     * @param url
     * @param jsonReq
     * @param charset
     * @return
     * @throws Exception
     */
    public String doPost(String url, String jsonReq, String charset) throws Exception {
        return doPost(url, jsonReq, charset, null);
    }

    /**
     * POST，application/json请求，HTTP、HTTPS均可；SSL需要证书，暂只实现双向认证
     * @param url 请求地址
     * @param jsonReq 请求body中的内容。json格式的字符串
     * @param charset 字符编码。例如utf-8
     * @param headers 请求头。key是请求头的名称，value是请求头的值
     * @return 请求成功后body中的内容
     */
    public String doPost(String url, String jsonReq, String charset, Map<String, String> headers) throws Exception {
        logger.info("POST url={} body={} header={}", url, jsonReq, headers == null ? null :headers.toString());

        createClient(url);
        checkClient();

        HttpPost httpPost = new HttpPost(url);

        StringEntity reqEntity = new StringEntity(jsonReq, charset);   //解决中文乱码问题
        reqEntity.setContentEncoding(charset);
        reqEntity.setContentType("application/json");
        httpPost.setEntity(reqEntity);

        // 添加请求头
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                httpPost.addHeader(key, headers.get(key));
            }
        }

        String recvData = null;
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(config);
            response = client.execute(httpPost) ;
            HttpEntity entity = response.getEntity();
            int respCode = response.getStatusLine().getStatusCode();
            if (respCode != HttpStatus.SC_OK) {
                throw new BizException("HTTP Status Code is " + respCode);
            }

            if (entity != null) {
                recvData = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.debug("Response content: " + recvData);
            }
            EntityUtils.consume(entity);
        }finally {
            if(response != null) {
                response.close();
            }
            this.client.close();
        }
        return recvData;
    }

    /**
     * GET请求
     *
     * @param url
     * @param charset
     * @return
     * @throws Exception
     */
    public String doGet(String url, String charset) throws Exception {
        createClient(url);
        checkClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        String recvData = null;
        CloseableHttpResponse response = null;
        try{

            response = this.client.execute(httpGet);
            int respCode = response.getStatusLine().getStatusCode();

            if(respCode != HttpStatus.SC_OK) {
                throw new BizException("HTTP Status Code is " + respCode);
            }
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                recvData = EntityUtils.toString(resEntity,charset);
            }
            EntityUtils.consume(resEntity);
        }finally {
            if(response != null) {
                response.close();
            }
            this.client.close();
        }
        return recvData;
    }

    private void checkClient() {
        if(this.client == null) {
            throw new BizException("failed to create http client");
        }
    }

    public static void main(String[] args) {
        // 生产证书密钥密文
        String keypass = args[0];
        String encPass = DesUtil.desCbcEncode(keypass, KEY);
        System.out.println("Cipher text is: " + encPass);
    }
}
