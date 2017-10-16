package com.raincc.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.WxUser;

public class WxLoginInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Integer wxUin = (Integer) ai.getController().getSession().getAttribute(TConstants.WX_UIN);
		if(wxUin == null ){
			ai.getController().redirect("/robotAdmin/rob/collect");
			return;
		}
		WxUser user = WxUser.dao.findFirst("select wxUserId,Uin from rb_wxuser where Uin = ? ",wxUin);
		String ajax = ai.getController().getRequest().getHeader("X-Requested-With");
		if("XMLHttpRequest".equals(ajax)){
			if(user == null){
				JSONObject jo = new JSONObject();
				jo.put("code", "-99");
				jo.put("info", "请先登录微信");
				ai.getController().renderJson(jo);
				return;
			}
		} else {
			if(user == null){
				ai.getController().redirect("/robotAdmin/rob/collect");
				return;
			}
		}
		ai.invoke();
	}

}
