package com.raincc.interceptor;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.raincc.common.RcConstants;
import com.raincc.model.Admin;
import com.raincc.robot.util.JsonUtils;

public class JdCliInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Admin admin = (Admin) ai.getController().getSession().getAttribute(RcConstants.session_login_adminCli);
		if (admin != null) {
			Integer jdunion = admin.getInt("JDtemporaryId");
			if(jdunion != null && jdunion > 0){
				ai.invoke();
			}else {
				JSONObject j = new JSONObject();
				j.put("code", "-100");
				j.put("info", "请先在后台登录京东。");
				JsonUtils.outPutJson( ai.getController().getResponse(),j);
				ai.getController().renderNull();
				return;
			}
		}else {
			JSONObject j = new JSONObject();
			j.put("code", "-100");
			j.put("info", "请先登录客户端。");
			JsonUtils.outPutJson( ai.getController().getResponse(),j);
			ai.getController().renderNull();
			return;
		}
		ai.invoke();
	}

}
