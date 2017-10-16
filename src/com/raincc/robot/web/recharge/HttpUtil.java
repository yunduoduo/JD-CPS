package com.raincc.robot.web.recharge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;



public class HttpUtil {
	
	
	@SuppressWarnings({ "deprecation", "resource" })
	public static void doGetStr(String url){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet =  new HttpGet(url);
		
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity  = response.getEntity();
			System.out.println("entity--------"+entity);
			if(entity!=null){
				String result = EntityUtils.toString(entity,"UTF-8");
				System.out.println("result---------"+result);
				
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings({ "deprecation", "resource" })
	public static String doPostStr(String url,String P0_biztype,String Cust_id  ,String P2_mobile,
			String P3_parvalue,String P4_productcode,String P5_requestid,String P6_callbackurl,String P7_extendinfo,String hmac){
		
				
		url = url+"?P0_biztype="+P0_biztype+"&Cust_id="+Cust_id+"&P2_mobile="+P2_mobile+"&P3_parvalue="+P3_parvalue+
				"&P4_productcode="+P4_productcode+"&P5_requestid="+P5_requestid+"&P6_callbackurl="+P6_callbackurl
				+"&P7_extendinfo="+P7_extendinfo+"&hmac="+hmac;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost  = new HttpPost(url);
		String result ="";
		try {
			HttpResponse response = httpClient.execute(httpPost);
			result = EntityUtils.toString(response.getEntity(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
