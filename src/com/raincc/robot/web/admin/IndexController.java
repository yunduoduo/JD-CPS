package com.raincc.robot.web.admin;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONObject;

import org.apache.commons.lang.RandomStringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mysql.fabric.xmlrpc.base.Array;
import com.raincc.common.RcConstants;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.model.Admin;
import com.raincc.model.AdminRole;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.jd.core.jdutils.TUtils;
import com.raincc.wx.action.CommonAction;
import com.raincc.wx.action.QrcodeAction;
import com.raincc.wx.pojo.AccessToken;
import com.raincc.wx.util.ConfKit;

@ControllerBind(controllerKey = "/robotAdmin/index", viewPath = "admin")
@Before(AdminInterceptor.class)
public class IndexController extends AdminBaseController {
	
	public void index() {
		setAttr("random", new Random().nextInt(10));
		Admin admin = (Admin)getSession().getAttribute(RcConstants.session_login_admin);
		int roleId = admin.getInt("roleId");
		AdminRole role = AdminRole.dao.findById(roleId);
		setAttr("isRoot",role.getBoolean("isRoot"));
		
		List<String> myUrls = Db.query(
				"select viewUrl " +
				"from rc_permission p, rc_rolePermissionRelation r " +
				"where p.permissionId = r.permissionId and r.roleId = ? ", roleId);
		String[] roleStrings = {"realEstate","personJD","logRecord","system"};
		String[] roleName    = {"京东采集群发","订单查询","日志记录","后台管理"};
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < roleStrings.length; i++) {
			if(role.getBoolean("isRoot")){//管理员拥有所有权限
				JSONObject jo = new JSONObject();
				jo.put("name",roleName[i]);
				jo.put("value",roleStrings[i]);
				list.add(jo);
			}else {
				if(myUrls.contains(roleStrings[i])){
					JSONObject jo = new JSONObject();
					jo.put("name",roleName[i]);
					jo.put("value",roleStrings[i]);
					list.add(jo);
				}
			}
			
		}
		setAttr("roleList",list);
		renderFreeMarker("index.html");
		
	}
	
	public void showAToken() {
		AccessToken token = CommonAction.getAccessToken(ConfKit.AppId, ConfKit.AppSecret);
		System.out.println("access_token-----"+token);
		renderText(token.getToken());
		
		List<Record> rs = Db.find("show variables like '%char%'");
		for (Record r : rs) {
			System.out.println(r);
		}
	}
	
	

	
	
	public void getSenceUrl() {
		String id = getPara("id");
		String url = QrcodeAction.getLimitSceneQrCodeUrl("wxfa542af71d7f529f", "f7c8a99417c152e42587e076dab9f3fd", "FOLLOW"+id);
		System.out.println(url);
		renderHtml(url);
		
	}
	
	public void sendCaptcha() {
		String mobile = getPara("mobile");
		String code = RandomStringUtils.randomNumeric(6);
//		SmsKit.sendCaptcha(mobile, code);
		
		renderText("已发" + code);
	}

}
