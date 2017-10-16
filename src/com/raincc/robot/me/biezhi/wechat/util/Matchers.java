package com.raincc.robot.me.biezhi.wechat.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.raincc.robot.entity.WxWechatMeta;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;

public class Matchers {

	public static String match(String p, String str){
		Pattern pattern = Pattern.compile(p);
		Matcher m = pattern.matcher(str);
		if(m.find()){
		    return m.group(1);
		}
		return null;
	}
	
//	WxWechatMeta对象转换
	public static WechatMeta wxWeToWech(WxWechatMeta wx){
		WechatMeta wch = new WechatMeta();
		wch.setBase_uri(wx.getStr("base_uri"));
		wch.setRedirect_uri(wx.getStr("redirect_uri"));
		wch.setWebpush_url(wx.getStr("webpush_url"));
		wch.setUuid(wx.getStr("uuid"));
		wch.setSkey(wx.getStr("skey"));
		wch.setSynckey(wx.getStr("synckey"));
		wch.setWxsid(wx.getStr("wxsid"));
		wch.setWxuin(wx.getStr("wxuin"));
		wch.setPass_ticket(wx.getStr("pass_ticket"));
		wch.setDeviceId(wx.getStr("deviceId"));
		wch.setCookie(wx.getStr("cookie"));
		wch.setBaseRequest(JSONObject.parseObject(wx.getStr("baseRequest")));
		wch.setSyncKey(JSONObject.parseObject(wx.getStr("OSyncKey")));
		wch.setUser(JSONObject.parseObject(wx.getStr("User")));
		return wch;
	}
	
}
