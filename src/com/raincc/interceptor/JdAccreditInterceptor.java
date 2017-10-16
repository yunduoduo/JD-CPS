package com.raincc.interceptor;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.log.Logger;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.JdPermission;

public class JdAccreditInterceptor implements Interceptor {

	private static final Logger _log = Logger.getLogger(JdAccreditInterceptor.class);
	
	@Override
	public void intercept(ActionInvocation ai) {
		//使用授权之前，要传递原请求地址JD_BACKURL
		String backUrl = ai.getController().getPara(TConstants.JD_STRINGBACKURL);
		if(StringUtils.isNotBlank(backUrl)){
			TConstants.JD_BACKURL = backUrl;
		}
		String token = ai.getController().getCookie(TConstants.JD_ACCESS_TOKEN);
		if(StringUtils.isNotBlank(token)){
			ai.invoke();
			return;
		}
		JdPermission jp = JdPermission.dao.findFirst("select JD_AccessToken from rb_jdpermission where id = 1 and tokenValidTime > UNIX_TIMESTAMP() ");
		if(jp != null){
			if(StringUtils.isNotBlank(jp.getStr("JD_AccessToken"))){
				ai.getController().setCookie(TConstants.JD_ACCESS_TOKEN,jp.getStr("JD_AccessToken"),1 * 24 * 60 * 60);
				ai.invoke();
				return;
			}
		}
		HttpServletRequest request = ai.getController().getRequest();
		String code = request.getParameter("code");
		if(StringUtils.isBlank(code)){
			String url = "https://oauth.jd.com/oauth/authorize?response_type=code&client_id={YOUR_CLIENT_ID}&redirect_uri={YOUR_REGISTERED_REDIRECT_URI}&state={YOUR_CUSTOM_CODE}";
			url = url.replace("{YOUR_CLIENT_ID}",TConstants.JD_Appkey)
					.replace("{YOUR_REGISTERED_REDIRECT_URI}", TConstants.JD_Accredit_callbackURL)
					.replace("{YOUR_CUSTOM_CODE}", "snsapi_base");
			_log.info("转向授权页：" + url);
			ai.getController().redirect(url);
			return;
		}else {
//			String url ="https://oauth.jd.com/oauth/token?grant_type=authorization_code&client_id="+TConstants.JD_Appkey
//					+"&client_secret="+ TConstants.JD_AppSecret
//					+"&scope=read&redirect_uri="+  TConstants.JD_Accredit_callbackURL
//					+"&code="+ code
//					+"&state=snsapi_base";
//					HttpRequest con = HttpUtils.request(url, "POST");//请求订单页面
//					con.header("Accept-Charset","utf-8");
//					String body = con.body();
//					JSONObject jo = JSONObject.parseObject(body);
//					if(StringUtils.isNotBlank(jo.getString("access_token"))){
//						ai.getController().setCookie(TConstants.JD_ACCESS_TOKEN,jo.getString("access_token"),1 * 24 * 60 * 60);
//						if(StringUtils.isNotBlank(TConstants.JD_BACKURL)){
//							String back = TConstants.JD_BACKURL;
//							TConstants.JD_BACKURL = "";
//							ai.getController().redirect(back);
//							return;
//						}
//					}
//					
//					/**
//					 * {"code":"402","error_description":"错误的code: xonZa5"}
//					 * {
//						  "access_token": "f5a9dd56-e23a-41a8-ab22-620ece16e9dc",
//						  "code": 0,
//						  "expires_in": 86399,
//						  "refresh_token": "540dfddb-4f91-4792-b964-199f1e7a5e3b",
//						  "time": "1501582524346",
//						  "token_type": "bearer",
//						  "uid": "8129226775",
//						  "user_nick": "huangwenke0703"
//						}
//					 */
		}
		ai.invoke();
	}

}
