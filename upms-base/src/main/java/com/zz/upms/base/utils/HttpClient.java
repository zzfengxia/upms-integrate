package com.zz.upms.base.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClient {
	private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);

	private CloseableHttpClient createSSLClientDefault(){
		try {
             SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                 //信任所有
                 public boolean isTrusted(X509Certificate[] chain,
                                 String authType) throws CertificateException {
                     return true;
                 }
             }).build();
             SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
             return HttpClients.custom().setSSLSocketFactory(sslsf).build();
         } catch (KeyManagementException e) {
        	 logger.error("Error:", e);
         } catch (NoSuchAlgorithmException e) {
        	 logger.error("Error:", e);
         } catch (KeyStoreException e) {
        	 logger.error("Error:", e);
         }
         return  HttpClients.createDefault();
	}

	public String httpPostSsl(String url, String sendData, String charset, Map<String, String> headerMap){
        CloseableHttpClient httpclient = this.createSSLClientDefault();  

        String recvData = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();//设置请求和传输超时时间

            httpPost.setConfig(requestConfig);

            httpPost.setEntity(new StringEntity(sendData, charset));
            
            if(headerMap != null){
	            Iterator iterator = headerMap.keySet().iterator();
	            while(iterator.hasNext()) {
	            	Entry entry = (Entry)iterator.next();
	            	String key = (String)entry.getKey();
	            	String value = (String)entry.getValue();
	            	httpPost.setHeader(key, value);
	            }
            }
            httpPost.addHeader("Content-type","application/json; charset=utf-8");
			logger.info("request url:" + httpPost.getURI());
			logger.debug("request header:" + headerMap);
			logger.debug("request charset:" + charset);
			logger.info("request data:" + sendData);
			
            CloseableHttpResponse resp = httpclient.execute(httpPost);
            try {  
                HttpEntity entity = resp.getEntity();  
                
    			if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
    				logger.info("httpPost fail, status code = " + resp.getStatusLine().getStatusCode());
    			}
    				
    			if (entity != null) {  
                    recvData = EntityUtils.toString(entity, charset);
    				logger.info("Response content: " + recvData);
                }  
            } finally {  
            	if(resp != null){
            		resp.close();
            	}
            }  
        } catch (Exception e) {  
        	logger.error("Error:", e);
        	e.printStackTrace();
        } finally {  
            // 关闭连接,释放资源    
            try { 
            	if(httpclient != null){
            		httpclient.close(); 
            	}
            } catch (IOException e) {  
            	logger.error("Error:", e);
            }  
        }  
        return recvData;
	}


    public String httpPostSslNew(String url, String sendData, String charset, Map<String, String> headerMap) {

        CloseableHttpClient httpclient = this.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        String recvData = null;
        try {
            httpPost.setEntity(new StringEntity(sendData, charset));

            if (headerMap != null) {
                for (Entry<String, String> entry : headerMap.entrySet()) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    httpPost.setHeader(key, value);
                }
            }

            logger.debug("request url:" + httpPost.getURI());
            logger.debug("request header:" + headerMap);
            logger.debug("request charset:" + charset);
            logger.debug("request data:" + EntityUtils.toString(httpPost.getEntity(), charset));
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).build();
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse resp = httpclient.execute(httpPost);
            try {
                HttpEntity entity = resp.getEntity();

                if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    logger.debug("httpPost fail, status code = " + resp.getStatusLine().getStatusCode());
                }

                if (entity != null) {
                    recvData = EntityUtils.toString(entity, charset);
                    logger.info("Response content: " + recvData);
                }
            } finally {
                if (resp != null) {
                    resp.close();
                }
            }
        } catch (Exception e) {
            logger.error("Error:", e);
        } finally {
            // 关闭连接,释放资源
//        	httpclient.getConnectionManager().shutdown();
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                logger.error("Error:", e);
            }
        }
        return recvData;
    }
    public  String httpPost(String jsonStr, String url) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(jsonStr, "utf-8"));
        String respBody = null;
        try {
            logger.info("executing request " + httppost.getURI());
            logger.info("request data:" + jsonStr);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    respBody = EntityUtils.toString(entity, "utf-8");
                    logger.info("Response content: " + respBody);
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return respBody;
    }

    public  String httpPost(String jsonStr, String url,String chartset) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("data", jsonStr));
        UrlEncodedFormEntity uefEntity;
        String respBody = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, chartset);
            httppost.setEntity(uefEntity);
            logger.info("executing request " + httppost.getURI());
            logger.info("request data:" + URLDecoder.decode(jsonStr,chartset));
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    respBody = EntityUtils.toString(entity, chartset);
                    logger.info("Response content: " + respBody);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            logger.error("error : " + e.getMessage());
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("error : " + e.getMessage());
            }
        }
        return respBody;
    }

    public String httpPost(String url, String chartset, List<BasicNameValuePair> formparams) {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
//        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,2000);//连接时间
//        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,2000);//数据传输时间
        // 创建httppost
        HttpPost httppost = new HttpPost(url);
        // 创建参数队列
        UrlEncodedFormEntity uefEntity;
        String respBody = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, chartset);
            httppost.setEntity(uefEntity);
            logger.info("executing request " + httppost.getURI());
            logger.debug("request charset:" + chartset);
            logger.info("request data:" + EntityUtils.toString(httppost.getEntity()));
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    respBody = EntityUtils.toString(entity, chartset);
                    logger.info("Response content: " + respBody);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            logger.error("error : " + e.getMessage());
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("error : " + e.getMessage());
            }
        }
        return respBody;
    }


    public String httpPostSsl(String url, List<BasicNameValuePair> params, String charset){
        CloseableHttpClient httpclient = this.createSSLClientDefault();
        HttpPost httpPost = new HttpPost(url);
        String recvData = null;
        try {
            //添加参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, charset));



            logger.info("request url:" + httpPost.getURI());

            logger.debug("request charset:" + charset);
            logger.info("request params:" + params);

            CloseableHttpResponse resp = httpclient.execute(httpPost);
            try {
                HttpEntity entity = resp.getEntity();

                if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    logger.info("httpPost fail, status code = " + resp.getStatusLine().getStatusCode());
                }

                if (entity != null) {
                    recvData = EntityUtils.toString(entity, charset);
                    logger.info("Response content: " + recvData);
                }
            } finally {
                if(resp != null){
                    resp.close();
                }
            }
        } catch (Exception e) {
            logger.error("Error:", e);
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                if(httpclient != null){
                    httpclient.close();
                }
            } catch (IOException e) {
                logger.error("Error:", e);
            }
        }
        return recvData;
    }

    public static void main(String[] args) {
        String url ="https://open-wlx.tenpay.com/cgi-bin/hce/hce_check_login.cgi?charset=utf-8&nonce_str=20190127100000355491875056128000&format=json&sign=kdZMVn%2Fer8Ni0O9h4iiu2qo%2FXDhh8WNGjFd5nTKfgFJ0OGziXv%2BFP9xq1pgeJonKU6%2FCn8Ko4x5ReI%2FNMO2cKMz5IbSoOiPZ3M%2F%2BUOl74nvBud%2BiuH1zqdKuNAUNConFZgOpfaQk6VP70lm6Z0BI0l8%2F1taHlrQGaSxhBXXL%2FXM%3D&biz_data=%7B%22skey_type%22%3A%221%22%2C%22open_id%22%3A%22os3ZK5C738b8r6qcSlo_SeHg0hF8%22%2C%22s_tk%22%3A%225a52%22%2C%22skey%22%3A%22yx59CAAAAAEAAAACIUtNXE4XcRIuKgcEBJS%2Bt6FNXq21vMMaMgV7jM0oLpGOSAvps9USRNl4i%2Fj3opxD%22%2C%22minipro_appid%22%3A%22wx5ec688ee36528749%22%7D&version=1.0&sign_type=sha1withrsa&app_id=10000166&timestamp=2019-01-27%2019%3A53%3A08";
        String charset = "utf-8";
        HttpClient  httpClient = new HttpClient();

        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");
//        httpClient.httpGetSslNew(url, charset, null);
        httpClient.httpPostSsl(url, "000000", "utf-8",null);
	}

    public String  httpGet(String url, String charset) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String recvData =null;
        try {
            // 创建httpget.
            logger.info("http get : {}", url);
            HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间

            httpget.setConfig(requestConfig);

            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();

                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    logger.debug("httpGet fail, status code = " + response.getStatusLine().getStatusCode());
                }

                if (entity != null) {
                    recvData = EntityUtils.toString(entity, charset);
                    logger.info("Response content: " + recvData);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            logger.error("Error:", e);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("Error:", e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error:", e);
        } finally {
            // 关闭连接,释放资源
            try {
                if(httpclient != null){
                    httpclient.close();
                }
            } catch (IOException e) {
                logger.error("Error:", e);
            }
        }
        return recvData;
    }


    public String httpGetSsl(String url, String charset, Map<String, String> headerMap){
        CloseableHttpClient httpclient = this.createSSLClientDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        String recvData = null;
        try {
            if(headerMap != null){
                Iterator<Entry<String, String>> iterator = headerMap.entrySet().iterator();
                while(iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    String key = (String)entry.getKey();
                    String value = (String)entry.getValue();
                    httpGet.setHeader(key, value);
                }
            }

            logger.info("request url:" + httpGet.getURI());
            logger.debug("request header:" + headerMap);
            logger.debug("request charset:" + charset);

            CloseableHttpResponse resp = httpclient.execute(httpGet);
            try {
                HttpEntity entity = resp.getEntity();

                if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    logger.debug("httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                }

                if (entity != null) {
                    recvData = EntityUtils.toString(entity, charset);
                    logger.info("Response content: " + recvData);
                }
            } finally {
                if(resp != null){
                    resp.close();
                }
            }
        } catch (Exception e) {
            logger.error("Error:", e);
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源    
            try {
                if(httpclient != null){
                    httpclient.close();
                }
            } catch (IOException e) {
                logger.error("Error:", e);
            }
        }
        return recvData;
    }

    public String httpPostMethod(String jsonStr, String url, String charset) {
        String respBody = null;
        HttpURLConnection conn = null;
        BufferedOutputStream out = null;
        try {
            conn = HttpClientUtil.getHttpURLConnection(url);
            // 以post方式通信
            conn.setRequestMethod("POST");
            // 设置请求默认属性
            HttpClientUtil.setHttpRequest(conn);
            //设置连接超时时间
            conn.setConnectTimeout(50 * 1000);
            //设置从主机读取数据超时时间
            conn.setReadTimeout(50 * 1000);
            // Content-Type
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            out = new BufferedOutputStream(conn.getOutputStream());
            final int len = 1024; // 1KB
            logger.info("executing request " + url);
            logger.info("request data:" + URLDecoder.decode(jsonStr, charset));
            HttpClientUtil.doOutput(out, jsonStr.getBytes(charset), len);
            // 获取应答输入流
            respBody = HttpClientUtil.InputStreamTOString(conn.getInputStream(), charset);
            logger.info("Response content: " + respBody);
        } catch (IOException e) {
            logger.error("error : " + e.getMessage());
        } finally {
            // 关闭流
            try {
                out.close();
            } catch (IOException e) {
                logger.error("error : " + e.getMessage());
            }

        }
        return respBody;
    }


    
}
