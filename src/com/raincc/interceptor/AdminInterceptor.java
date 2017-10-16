package com.raincc.interceptor;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.common.RcConstants;
import com.raincc.model.Admin;
import com.raincc.model.AdminRole;

public class AdminInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {
		Admin admin = (Admin) ai.getController().getSession().getAttribute(RcConstants.session_login_admin);
		if (admin == null) {
			ai.getController().redirect("/rc/admin/login");
			return;
		}
		int roleId = admin.getInt("roleId");
		AdminRole role = AdminRole.dao.findById(roleId);
		if (!role.getBoolean("isRoot")) { // 需要去读权限
			List<String> myUrls = Db.query(
					"select viewUrl " +
					"from rc_permission p, rc_rolePermissionRelation r " +
					"where p.permissionId = r.permissionId and r.roleId = ? ", roleId);
			if (!myUrls.contains(ai.getActionKey())) {
				// 没有权限干此事
				String ajax = ai.getController().getRequest().getHeader("X-Requested-With");
				if("XMLHttpRequest".equals(ajax)){
					JSONObject jo = new JSONObject();
					jo.put("code", "-99");
					jo.put("info", "没有权限");
					ai.getController().renderJson(jo);
				} else {
					List<String> urls = Db.query(
							"select viewUrl " +
							"from rc_permission p, rc_rolePermissionRelation r " +
							"where p.permissionId <> r.permissionId and r.roleId = ? ", roleId);
					if("/robotAdmin/index".contains(ai.getActionKey()) || "/rc/admin/loguot".contains(ai.getActionKey())){
						ai.invoke();
					}else if(urls.contains(ai.getActionKey())){
//					}else{
						ai.getController().renderText("权限不足");
						return;
					}
				}
			} else {
//				List<String> myNames = Db.query(
//					"select permissionName " +
//					"from rc_permission p, rc_rolePermissionRelation r " +
//					"where p.permissionId = r.permissionId and r.roleId = ? ", roleId);
//				ai.getController().setAttr("__my_access", myNames);
			}
			
		}
		ai.getController().setAttr("__admin", admin);
		ai.getController().setAttr("pageTip", ai.getController().getSession().getAttribute("pageTip"));
		ai.getController().setAttr("pageTipState", ai.getController().getSession().getAttribute("pageTipState"));
		
		ai.getController().getSession().removeAttribute("pageTip");
		ai.getController().getSession().removeAttribute("pageTipState");
		ai.invoke();
		
	}

}
