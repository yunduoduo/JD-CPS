package com.raincc.kit;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.EncryptionKit;
import com.jfinal.kit.HttpKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.raincc.robot.config.TConstants;
import com.raincc.wx.CacheTimeout;

/**
 *	微社区相关接口功能 
 */
public class WsqKit {
	
	private static final String wsq_token = "wsq_token";
	
	private static final String wsq_user_access_token = "wsq_user_access_token";
	
	protected static final Logger _log = Logger.getLogger(WsqKit.class);
	
	public static String getAppToken() {
		String token = CacheKit.get(CacheTimeout.LRUCache_1h, wsq_token);
		if (token != null) {
			return token;
		}
		
		long time = System.currentTimeMillis()/1000;
		
		String sig = EncryptionKit.sha1Encrypt(TConstants.wsq_AppID + TConstants.wsq_AppSecret + time);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("appId", TConstants.wsq_AppID);
		params.put("sig", sig);
		params.put("time", time+"");
		params.put("type", "security");
		
		String result = HttpKit.post("https://openapi.wsq.qq.com/v1/app/token", params, "");
		_log.info("微社区token=" + result);
		
		JSONObject jo = JSON.parseObject(result);
		if (jo.getInteger("errCode") == 0) {
			JSONObject data = jo.getJSONObject("data");
			token = data.getString("appToken");
			CacheKit.put(CacheTimeout.LRUCache_1h, wsq_token, token);
		}
		
		return token;
	}
	
	public static String getUserAccessToken(String code) {
		String accessToken = CacheKit.get(CacheTimeout.LRUCache_1h, wsq_user_access_token);
		if (accessToken != null) {
			return accessToken;
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("appToken", getAppToken());
		params.put("code", code);
		
		String result = HttpKit.get("https://openapi.wsq.qq.com/v1/user/auth", params);
		_log.info("微信区accessToken=" + result);
		
		JSONObject jo = JSON.parseObject(result);
		if (jo.getInteger("errCode") == 0) {
			accessToken = jo.getJSONObject("data").getString("accessToken");
		}
		
		return accessToken;
		
	}
	
	public static Boolean threadNew(String code, String content, String[] picIds) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("appToken", getAppToken());
		params.put("sId", TConstants.wsq_sId);
		params.put("accessToken", getUserAccessToken(code));
		params.put("content", content);
		String picIdsStr = "";
		if (picIds != null && picIds.length > 0) {
			for (String picId : picIds) {
				if (picIdsStr.length() > 0) {
					picIdsStr += ",";
				}
				picIdsStr += picId;
			}
		}
		params.put("picIds", picIdsStr);
		
		String result = HttpKit.post("https://openapi.wsq.qq.com/v1/thread/new", params, "");
		JSONObject jo = JSON.parseObject(result);
		_log.info("微社区发表主题结果：" + jo);
		
		return true;
	}
	
	public static void main(String[] args) {
//		WsqKit.getToken();
	}

}
