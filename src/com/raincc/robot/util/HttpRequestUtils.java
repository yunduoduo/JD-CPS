package com.raincc.robot.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Logger;
import com.raincc.wx.util.MyX509TrustManager;

public class HttpRequestUtils {

	private static final Logger _log = Logger.getLogger(HttpRequestUtils.class);

	/**
	 * 建议用 HttpURLConnection
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty(
							"user-agent",
							"Mozilla/5.0 (iPad; CPU OS 4_3_5 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8L1 Safari/6533.18.5");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				_log.info(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(),"utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			_log.info("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 建议用HttpURLConnection
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param,String cookie,String ContentType) {

		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		URLConnection conn = null ;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.setRequestProperty("connection", "Keep-Alive");
            if(StringUtils.isNotBlank(ContentType)){
            	conn.setRequestProperty("Content-Type",ContentType);
            }
            conn.setRequestProperty("user-agent",
	                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
			if(StringUtils.isNotBlank(cookie)){
				conn.setRequestProperty("Cookie", cookie);
			}
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream());
			// 发送请求参数
			out.write(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			_log.info("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				_log.info("BufferedReader关闭异常。。。"+ex);
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	
	/**
	 * @param sendUrl
	 * @param param
	 * @param cookie
	 * @param ContentType
	 * @return
	 */
	public static String sendHttpUrl(String sendUrl,String param,String cookie,String ContentType){
		InputStream inputStream = null;
		OutputStreamWriter out = null;
		HttpURLConnection urlConnection = null;
		String body = "";
		try{
			 URL url = new URL(sendUrl);
			 urlConnection = (HttpURLConnection) url.openConnection();  
			 if(StringUtils.isNotBlank(ContentType)){
				 urlConnection.setRequestProperty("Content-type",ContentType);
			 }
			 if(StringUtils.isNotBlank(cookie)){
				 urlConnection.setRequestProperty("Cookie",cookie);
			 }
			 urlConnection.setRequestProperty("User-Agent",
	                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
			 urlConnection.setDoOutput(true);
			 out = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"); 
			 out.write(param);
	         out.flush();
	         inputStream = urlConnection.getInputStream();
//             String encoding = urlConnection.getContentEncoding();
             body = IOUtils.toString(inputStream, "UTF-8");
             _log.info("接口返回响应码--->"+urlConnection.getResponseCode());
//	         _log.info("响应体body--->"+body);
		  }catch(IOException e1){
			  _log.info("urlConnection调用出的异常");
			  e1.printStackTrace();
		  }finally{
			  if(urlConnection != null ){
				  urlConnection.disconnect();
			  }
			  try {
				  if(inputStream != null){
					  inputStream.close();
				  }
			} catch (IOException e) {
				_log.info("流关闭失败。。。");
				e.printStackTrace();
			}finally{
				  if(out != null){
					  try {
						out.close();
					} catch (IOException e) {
						_log.info("流关闭失败。。。");
						e.printStackTrace();
					}
				  }
			}
		  }
		return body;
	}
	
	/** 
     * 发起https请求并获取结果 
     *  
     * @param requestUrl 请求地址 
     * @param requestMethod 请求方式（GET、POST） 
     * @param outputStr 提交的数据 
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值) 
     */  
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr,
    		String cookie,String ContentType) {  
    	_log.info("请求数据：" + requestUrl + "," + requestMethod + "," + outputStr);
        JSONObject jsonObject = null;  
        StringBuffer buffer = new StringBuffer();  
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化  
            TrustManager[] tm = { new MyX509TrustManager() };  
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");  
            sslContext.init(null, tm, new java.security.SecureRandom());  
            // 从上述SSLContext对象中得到SSLSocketFactory对象  
            SSLSocketFactory ssf = sslContext.getSocketFactory();  
  
            URL url = new URL(requestUrl);  
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();  
            httpUrlConn.setSSLSocketFactory(ssf);
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）  
            httpUrlConn.setRequestMethod(requestMethod);
//            if(StringUtils.isNotBlank(ContentType)){
//            	httpUrlConn.setRequestProperty("Content-type",ContentType);
//			}
//			if(StringUtils.isNotBlank(cookie)){
//				httpUrlConn.setRequestProperty("Cookie",cookie);
//			}
            if ("GET".equalsIgnoreCase(requestMethod))  
                httpUrlConn.connect();  
  
            // 当有数据需要提交时  
            if (null != outputStr) {  
                OutputStream outputStream = httpUrlConn.getOutputStream();  
                // 注意编码格式，防止中文乱码
                outputStream.write(outputStr.getBytes("UTF-8"));  
                outputStream.close();  
            }  
  
            // 将返回的输入流转换成字符串  
            InputStream inputStream = httpUrlConn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
  
            String str = null;  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            bufferedReader.close();  
            inputStreamReader.close();  
            // 释放资源  
            inputStream.close();  
            inputStream = null;  
            httpUrlConn.disconnect();  
            jsonObject = JSONObject.parseObject(buffer.toString());  
        } catch (ConnectException ce) {  
        	ce.printStackTrace();
        	_log.error("Weixin server connection timed out.");  
        } catch (Exception e) {  
        	e.printStackTrace();
        	_log.error("https request error:{}", e);  
        }  
        _log.info("得到原始数据：" + jsonObject);
        return jsonObject;  
    }  
}
