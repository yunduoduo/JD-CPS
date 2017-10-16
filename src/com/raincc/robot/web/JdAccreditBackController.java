package com.raincc.robot.web;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.interceptor.JdAccreditInterceptor;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.jd.core.jdutils.HttpUtils;
import com.raincc.web.BaseController;
@ControllerBind(controllerKey = "/robot/accreditBack")
public class JdAccreditBackController extends BaseController{
	
	public void index(){
		HttpServletRequest request = getRequest();
		String code = request.getParameter("code");
		if(StringUtils.isBlank(code)){
			String url = "https://oauth.jd.com/oauth/authorize?response_type=code&client_id={YOUR_CLIENT_ID}&redirect_uri={YOUR_REGISTERED_REDIRECT_URI}&state={YOUR_CUSTOM_CODE}";
			url = url.replace("{YOUR_CLIENT_ID}",TConstants.JD_Appkey)
					.replace("{YOUR_REGISTERED_REDIRECT_URI}",TConstants.JD_Accredit_callbackURL)
					.replace("{YOUR_CUSTOM_CODE}", "snsapi_base");
			_log.info("转向授权页：" + url);
			redirect(url);
			return;
		}else {
			String url ="https://oauth.jd.com/oauth/token?grant_type=authorization_code&client_id="+TConstants.JD_Appkey
					+"&client_secret="+ TConstants.JD_AppSecret
					+"&scope=read&redirect_uri="+ TConstants.JD_Accredit_callbackURL
					+"&code="+ code
					+"&state=snsapi_base";
					HttpRequest con = HttpUtils.request(url, "POST");//请求订单页面
					con.header("Accept-Charset","utf-8");
					String body = con.body();
					if(body.contains("html") || body.contains("error_description")){
						_log.info("授权失败："+body);
						renderText(body);
						return;
					}
					_log.info("body:"+body);
					JSONObject joJsonObject = JSONObject.parseObject(body);
					String access_token = joJsonObject.getString("access_token");
					if(StringUtils.isNotBlank(access_token)){
						Integer tokenValidTime = (int)(new Date().getTime() / 1000) + joJsonObject.getInteger("expires_in");
						Db.update("update rb_jdpermission set upTime = ? , uid = ? ,user_nick=?,expires_in=?,JD_AccessToken=? ,tokenValidTime= ? ,refresh_token= ?,time = ? ,token_type = ?  where unionId = ? ",
								new Date(),joJsonObject.getString("uid"),joJsonObject.getString("user_nick"),joJsonObject.getInteger("expires_in"),
								joJsonObject.getString("access_token"),tokenValidTime,joJsonObject.getString("refresh_token"),
								joJsonObject.getString("time"),joJsonObject.getString("token_type"),TConstants.BL_JD_UNIONID);
						setCookie(TConstants.JD_ACCESS_TOKEN, access_token, 1 * 24 * 60 * 60);
						if(StringUtils.isNotBlank(TConstants.JD_BACKURL)){
							String back = TConstants.JD_BACKURL;
							TConstants.JD_BACKURL = "";
							redirect(back);
							return;
						}
					}
					renderText(body);return;
			}
		}
	@Before(JdAccreditInterceptor.class)
	public void jdAccredit(){
		_log.info("授权成功。。。,token="+getCookie(TConstants.JD_ACCESS_TOKEN));
		renderNull();return;
	}
}
