package com.zz.upms.base.utils;

import com.zz.upms.base.common.exception.BizException;
import com.zz.upms.base.common.exception.ErrorCode;
import org.apache.http.HttpEntity;
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
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
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

    // 超时重试次数
    private static final int TIMEOUT_RETRY_COUNT = 3;

    private String trustPath;
    private String trustStorePassword;
    private String keyPath;
    private String keyStorePassword;
    private String keyPassword;
    private CloseableHttpClient client;
    private RequestConfig config;
    //private String url;

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
        if (config == null) {
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
     * @param keyPath            密钥库地址
     * @param keyStorePassword   密钥库文件密码
     * @param keyPassword        私钥密码
     * @param trustPath          信任库地址
     * @param trustStorePassword 信任库密码
     * @param timeOut            超时时间
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
     * @param keyPath          密钥库地址
     * @param keyStorePassword 密钥库文件密码
     * @param keyPassword      私钥密码
     * @param timeOut          超时时间
     */
    public CustomApacheHttpClient(String keyPath, String keyStorePassword, String keyPassword, int timeOut) {
        this(timeOut);
        this.keyPath = keyPath;
        this.keyStorePassword = DesUtil.desCbcDecode(keyStorePassword, KEY);
        this.keyPassword = DesUtil.desCbcDecode(keyPassword, KEY);
    }

    private void createClient() {
        this.client = HttpClientBuilder.create()
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .setDefaultRequestConfig(config)
                .build();
    }

    private void createSSLClient() {
        if (keyPath != null && keyPassword != null && keyStorePassword != null && trustPath != null && trustStorePassword != null) {
            createSSLClientWithBothCert();
            return;
        }
        if (keyPath != null && keyPassword != null && keyStorePassword != null && trustPath == null && trustStorePassword == null) {
            createSSLClientWithBothNoTrustCert();
            return;
        }
        if (keyPath == null && keyPassword == null && keyStorePassword == null && trustPath == null && trustStorePassword == null) {
            createSSLClientWithSingleNoCert();
        }
    }

    /**
     * https单向认证忽略服务器证书
     */
    private void createSSLClientWithSingleNoCert() {
        try {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(new org.apache.http.conn.ssl.TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build();

            // 这里填多个协议时会报 handshake_failure 异常
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                    new String[]{"TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslsf)
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

            this.client = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(cm)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler())
                    .setConnectionManagerShared(true)
                    .build();
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    /**
     * https双向认证忽略服务器证书
     */
    private void createSSLClientWithBothNoTrustCert() {
        // 服务端支持的安全协议
        String[] supportedProtocols = {"TLSv1.2"};
        SSLConnectionSocketFactory socketFactory = null;
        try {
            KeyStore keyStore = CertUtil.getKeyStoreByCert(keyPath, keyStorePassword);

            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(new org.apache.http.conn.ssl.TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).loadKeyMaterial(keyStore, keyPassword.toCharArray()).build();

            socketFactory = new SSLConnectionSocketFactory(sslContext, supportedProtocols, null,
                    NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", socketFactory)
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);

            this.client = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .setConnectionManager(cm)
                    .setConnectionManagerShared(true)
                    .setDefaultRequestConfig(config)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                    .build();
        } catch (Exception e) {
            logger.error("Error:", e);
        }
    }

    /**
     * https双向认证带证书
     */
    private void createSSLClientWithBothCert() {
        // 服务端支持的安全协议
        String[] supportedProtocols = {"TLSv1.2"};
        SSLConnectionSocketFactory socketFactory = null;
        try {
            KeyStore truststore = CertUtil.getKeyStoreByCert(trustPath, trustStorePassword);
            KeyStore keyStore = CertUtil.getKeyStoreByCert(keyPath, keyStorePassword);

            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(truststore, null)
                    .loadKeyMaterial(keyStore, keyPassword.toCharArray()).build();
            socketFactory = new SSLConnectionSocketFactory(sslContext, supportedProtocols, null,
                    NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", socketFactory)
                    .build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);

            this.client = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .setConnectionManager(cm)
                    .setConnectionManagerShared(true)
                    .setDefaultRequestConfig(config)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error:", e);
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
        if (url.substring(0, 5).equalsIgnoreCase("https")) {
            createSSLClient();
        } else {
            createClient();
        }
        if (this.client == null) {
            throw new BizException("Failed to create Http client");
        }

        HttpPost httpPost = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keys = formparams.keySet();
        for (String key : keys) {
            nvps.add(new BasicNameValuePair(key, formparams.get(key)));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));

        String recvData = null;
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(config);
            response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int respCode = response.getStatusLine().getStatusCode();
            if (respCode != HttpStatus.SC_OK) {
                throw new BizException("Failed to execute Http request: " + respCode);
            }

            if (entity != null) {
                recvData = EntityUtils.toString(entity, "utf-8");
                logger.info("Response content: " + recvData);
            }
            EntityUtils.consume(entity);
        } finally {
            if (response != null) {
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

        if(url.substring(0, 5).equalsIgnoreCase("https")) {
            createSSLClient();
        }else {
            createClient();
        }
        if(this.client == null) {
            throw new BizException(ErrorCode.BIZ_ERROR);
        }

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
                throw new BizException(ErrorCode.BIZ_ERROR);
            }

            if (entity != null) {
                recvData = EntityUtils.toString(entity, "utf-8");
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
        if (url.substring(0, 5).equalsIgnoreCase("https")) {
            createSSLClient();
        } else {
            createClient();
        }
        if (this.client == null) {
            throw new BizException("Failed to create Http client");
        }

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        String recvData = null;
        CloseableHttpResponse response = null;
        try {

            response = this.client.execute(httpGet);
            int respCode = response.getStatusLine().getStatusCode();

            if (respCode != HttpStatus.SC_OK) {
                throw new BizException("Failed to execute Http request,error code: " + respCode);
            }
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                recvData = EntityUtils.toString(resEntity, charset);
            }
        } finally {
            if (response != null) {
                response.close();
            }
            this.client.close();
        }
        return recvData;
    }

    /**
     * get请求
     *
     * @param url
     * @param cookies
     * @param headers
     * @param charset
     * @return
     * @throws Exception
     */
    public String doGet(String url, Map<String, String> cookies, Map<String, String> headers, String charset) throws Exception {
        if (url.substring(0, 5).equalsIgnoreCase("https")) {
            createSSLClient();
        } else {
            createClient();
        }
        if (this.client == null) {
            throw new BizException("Failed to create Http client");
        }

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        // 添加cookies
        if(cookies != null) {
            String cookiesStr = mapToString(cookies);
            httpGet.addHeader("Cookie", cookiesStr);
        }
        // 添加http headers
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                httpGet.addHeader(key, headers.get(key));
            }
        }

        String recvData = null;
        CloseableHttpResponse response = null;
        try {

            response = this.client.execute(httpGet);
            int respCode = response.getStatusLine().getStatusCode();

            if (respCode != HttpStatus.SC_OK) {
                throw new BizException("Failed to execute Http request,error code: " + respCode);
            }
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                recvData = EntityUtils.toString(resEntity, charset);
            }
        } finally {
            if (response != null) {
                response.close();
            }
            this.client.close();
        }
        return recvData;
    }

    private static String mapToString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = params.keySet();
        boolean isFirst = true;
        for (String key : keys) {
            if(isFirst) {
                isFirst = false;
            } else {
                sb.append(";");
            }
            sb.append(key);
            sb.append("=");
            sb.append(params.get(key));
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        // 生产证书密钥密文
        String keypass = args[0];
        String encPass = DesUtil.desCbcEncode(keypass, KEY);
        System.out.println("Cipher text is: " + encPass);
    }
}
