//package com.raincc.robot.util;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.apache.http.ssl.TrustStrategy;
//import org.apache.http.util.EntityUtils;
//
//import javax.net.ssl.SSLContext;
//import java.io.IOException;
//import java.security.KeyManagementException;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//
///**
// * Created by jiangtengfei on 16/3/2.
// */
//public class HttpClientHttpsTest {
//
//    public static void httpsTest() {
//
//        CloseableHttpClient httpClient = null;
//
//        try {
//            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
//                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//                    return true;
//                }
//            }).build();
//
//            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
//            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
//
//
//            if (httpClient != null) {
//                HttpGet httpGet = new HttpGet("https://www.baidu.com");
//
//                CloseableHttpResponse response = httpClient.execute(httpGet);
//
//                try {
//                    HttpEntity entity = response.getEntity();
//                    System.out.println("--------------------------------------");
//                    if (entity != null) {
//                        System.out.println("Response content length: " + entity.getContentLength());
//                        System.out.println(EntityUtils.toString(entity));
//                        EntityUtils.consume(entity);
//                    }
//                } finally {
//                    response.close();
//                }
//            }
//
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void main(String[] args) {
//        HttpClientHttpsTest.httpsTest();
//    }
//}
