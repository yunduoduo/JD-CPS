package com.raincc.interceptor;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.raincc.common.RcConstants;
import com.raincc.model.Admin;
import com.raincc.robot.util.JsonUtils;

public class AdminCliInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Admin admin = (Admin) ai.getController().getSession().getAttribute(RcConstants.session_login_adminCli);
		if (admin == null) {
			JSONObject j = new JSONObject();
			j.put("code", "-100");
			j.put("info", "请先登录客户端");
			JsonUtils.outPutJson( ai.getController().getResponse(),j);
			ai.getController().renderNull();
			return;
		}
		ai.invoke();
	}

}
