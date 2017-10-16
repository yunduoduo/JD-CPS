package com.raincc.robot.me.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSONObject;

public class test {
	public static void main(String[] args) {
//	    String test = "sdaasd:asdfasdf:{}[]zsdvf:''zsdc最代码网站地址:www.zuidaima.com";
//	 
//	    String unicode = string2Unicode(test);
//	     
//	    String string = unicode2String(unicode) ;
//	     
//	    System.out.println(unicode);
//	     
//	    System.out.println(string);
	 
		String aString = "webwx_auth_ticket=CIsBEIXFh9sIGoABUa7JOgVPcGuchxY3Wv6zM97zm4gps1P1ha8VhTavPuMup8bGBTPzQ43IfnjPlIrC/tgLg6WvW6k0MfYTFFVyIy50Gk3deZ2Ccm+sXOjGL3LQQMmr+cMi7wV5DNG4bY132cybfpkxw6xtBDJFIir8AuhEl6Zr1w77mGDkJdRchII=;webwxuvid=676f0c6abf18ad0e95c0550eefa21269f5623b59b69632071971d0d5dd280e04453af06cdc0ace9404fa946e91a6962b;webwx_data_ticket=gSeofoXEWiRNGy5t1Pso2oJk;mm_lang=zh_CN;wxloadtime=1500979703;wxsid=N60d6HUG/rk72qZC;wxuin=1403363882;";
		System.err.println(aString.length());
		
		
	}
	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String unicode) {
	 
	    StringBuffer string = new StringBuffer();
	 
	    String[] hex = unicode.split("\\\\u");
	 
	    for (int i = 1; i < hex.length; i++) {
	 
	        // 转换出每一个代码点
	        int data = Integer.parseInt(hex[i], 16);
	 
	        // 追加成string
	        string.append((char) data);
	    }
	 
	    return string.toString();
	}
	/**
	 * 字符串转换unicode
	 */
	public static String string2Unicode(String string) {
	 
	    StringBuffer unicode = new StringBuffer();
	 
	    for (int i = 0; i < string.length(); i++) {
	 
	        // 取出每一个字符
	        char c = string.charAt(i);
	 
	        // 转换为unicode
	        unicode.append("\\u" + Integer.toHexString(c));
	    }
	 
	    return unicode.toString();
	}
}
