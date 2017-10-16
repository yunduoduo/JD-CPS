package com.raincc.interceptor;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

public class FreemarkerReqParamInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		ai.invoke();
		Map<String, String> param = new LinkedHashMap<String, String>();
		String query = ai.getController().getRequest().getQueryString();
		for (Enumeration<String> paras=ai.getController().getParaNames(); paras.hasMoreElements();) {
			String attrName = paras.nextElement();
			param.put(attrName, ai.getController().getRequest().getParameter(attrName));
		}
		ai.getController().setAttr("param", param);
		ai.getController().setAttr("_queryParam", query == null ? "" : "?" + query);
//		Integer userId = ai.getController().getCookieToInt(TConstants.session_login_userId);
//		String str = ai.getController().getCookie(TConstants.session_login_str);
//		User user  = User.dao.findFirst("select * from robot_user where str = ? ",str);
//		if(user!=null){
//			ai.getController().setAttr("userId",user.get("userId"));
//		}
	}

}
