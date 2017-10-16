package com.raincc.robot.me.biezhi.wechat;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.base.Config;
import com.raincc.robot.me.biezhi.wechat.model.WechatContact;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;

public class Constant {

	public static Boolean  boStop = true;
	public static final String HTTP_OK = "200";
	public static final String BASE_URL = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
	public static final String JS_LOGIN_URL = "https://login.weixin.qq.com/jslogin";
	public static final String QRCODE_URL = "https://login.weixin.qq.com/qrcode/";
	public static final String ITPK_API = "http://i.itpk.cn/api.php";
//	public static final String CK_WECHATMETA = "CK_WECHATMETA";
//	public static final String CK_WECHATMETA_FLOCK = "CK_WECHATMETA_FLOCK";
	// 特殊用户 须过滤
	public static final List<String> FILTER_USERS = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail", 
			"fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", 
			"readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", 
			"weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts",
			"notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", 
			"notification_messages");
	
	public static final String[] SYNC_HOST = {
		"webpush.weixin.qq.com",
		"webpush2.weixin.qq.com",
		"webpush.wechat.com",
		"webpush1.wechat.com",
		"webpush2.wechat.com",
		"webpush1.wechatapp.com"
	};
	
	public static WechatContact CONTACT;
	
	// 全局配置文件
	public static Config config;
	
	/**
	 * unicode 转字符串
	 */
	public static String unicodeToString(String unicode) {
	 
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
	public static String stringToUnicode(String string) {
	 
	    StringBuffer unicode = new StringBuffer();
	 
	    for (int i = 0; i < string.length(); i++) {
	 
	        // 取出每一个字符
	        char c = string.charAt(i);
	 
	        // 转换为unicode
	        unicode.append("\\u" + Integer.toHexString(c));
	    }
	 
	    return unicode.toString();
	}
	
	/**
	 * WechatMeta 转json
	 * @param wechatMeta
	 *  
	 */
	public static JSONObject objectTojson(WechatMeta wechatMeta) {
		 JSONObject jObject = new JSONObject();
		 jObject.put("base_uri",wechatMeta.getBase_uri());
		 jObject.put("redirect_uri",wechatMeta.getRedirect_uri());
		 jObject.put("webpush_url",wechatMeta.getWebpush_url());
		 jObject.put("uuid",wechatMeta.getUuid());
		 jObject.put("skey",wechatMeta.getSkey());
		 jObject.put("synckey",wechatMeta.getSynckey());
		 jObject.put("wxsid",wechatMeta.getWxsid());
		 jObject.put("wxuin",wechatMeta.getWxuin());
		 jObject.put("pass_ticket",wechatMeta.getPass_ticket());
		 jObject.put("deviceId",wechatMeta.getDeviceId());
		 jObject.put("cookie",wechatMeta.getCookie());
		 String BaseRequest = wechatMeta.getBaseRequest().toString();
		 jObject.put("baseRequest",BaseRequest);
		 String SyncKey = wechatMeta.getSynckey().toString();
		 jObject.put("SyncKey",SyncKey);
		 String User = wechatMeta.getUser().toString();
		 jObject.put("User",User);
		 return jObject;
	}
	/**
	 * json转WechatMeta对象
	 * @param jObject
	 * @return
	 */
	public static WechatMeta JsonToObject(JSONObject jObject) {
		WechatMeta wechatMeta = new WechatMeta();
		wechatMeta.setBase_uri(jObject.getString(""));
		wechatMeta.setRedirect_uri(jObject.getString(""));
		wechatMeta.setWebpush_url(jObject.getString(""));
		wechatMeta.setUuid(jObject.getString(""));
		wechatMeta.setSkey(jObject.getString(""));
		wechatMeta.setSynckey(jObject.getString(""));
		wechatMeta.setWxsid(jObject.getString(""));
		wechatMeta.setWxuin(jObject.getString(""));
		wechatMeta.setPass_ticket(jObject.getString(""));
		wechatMeta.setDeviceId(jObject.getString(""));
		wechatMeta.setCookie(jObject.getString(""));
//		wechatMeta.setBaseRequest(jObject.getJSONObject(""));
//		wechatMeta.setSynckey(jObject.getString(""));
//		wechatMeta.setUser(jObject.getString(""));
		return wechatMeta;
	}
}
