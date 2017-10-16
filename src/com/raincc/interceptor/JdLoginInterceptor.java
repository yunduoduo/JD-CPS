package com.raincc.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.WxJdUser;

public class JdLoginInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Integer wxJdUserId = (Integer) ai.getController().getSession().getAttribute(TConstants.JD_USERID);
		if(wxJdUserId == null || wxJdUserId <= 0){
			ai.getController().redirect("/robotAdmin/rob/jd/login");
			return;
		}
		WxJdUser user = WxJdUser.dao.findFirst("select accountsId from rb_wxjduser where accountsId = ? ",wxJdUserId);
		String ajax = ai.getController().getRequest().getHeader("X-Requested-With");
		if("XMLHttpRequest".equals(ajax)){
			if(user == null){
				JSONObject jo = new JSONObject();
				jo.put("code", "-99");
				jo.put("info", "请先登录京东");
				ai.getController().renderJson(jo);
				return;
			}
		} else {
			if(user == null){
				ai.getController().redirect("/robotAdmin/rob/jd/login");
				return;
			}
		}
		ai.invoke();
	}

}
