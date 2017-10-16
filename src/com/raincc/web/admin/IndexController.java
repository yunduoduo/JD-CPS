package com.raincc.web.admin;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.common.RcConstants;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.kit.CommonUtilsKit;
import com.raincc.model.Admin;
import com.raincc.model.AdminRole;
import com.raincc.robot.entity.LogType;
import com.raincc.robot.entity.UserLogs;
import com.raincc.web.BaseController;

@ControllerBind(controllerKey = "/rc/admin")
@Before(AdminInterceptor.class) 
public class IndexController extends BaseController {
	
	@ClearInterceptor(ClearLayer.ALL)
	@Before(Tx.class)
	public void login() {
		Admin s_admin = (Admin) getSession().getAttribute(RcConstants.session_login_admin);
		if (s_admin != null) {
			redirect(RcConstants.admin_index_url);
			return;
		}
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			String account = getPara("account");
			String password = getPara("password");
			Admin admin = Admin.dao.findFirst("select * from rc_admin where isValid=1 and account = ? and password = ?", account, password);
			if (admin == null) {
				setAttr("error", "账号或密码错误");
				_log.warn("AdminLogin::" + account + "," + password + ",FAIL");
			} else {
				getSession().setAttribute(RcConstants.session_login_admin, admin);
				admin.set("lastLoginDate", new Date());
				admin.set("lastLoginIp", CommonUtilsKit.getIpAddr(getRequest()));
				admin.update();
				String logInfo = "帐号登录：【账户“"+admin.getStr("account")+"”登录成功。】";
				UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, admin.getInt("adminId"), admin.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
				_log.info("AdminLogin::" + account + "," + admin.getStr("lastLoginIp") + ",SUC");
				redirect(RcConstants.admin_index_url);
			}
		}
	}
	
	public void loguot() {
		getSession().invalidate();
		redirect("/rc/admin/login");
	}
	@Before(Tx.class)
	public void valid() {
		int adminId = getParaToInt("adminId");
		Admin admin = Admin.dao.findById(adminId);
		admin.set("isValid", !admin.getBoolean("isValid"));
		admin.update();
		Admin adminDB = getAdmin();
		String aString = admin.getBoolean("isValid") ? "有效":"无效";
		String logInfo = "设置帐号是否有效状态：【设置账户“"+admin.getStr("account")+"”状态为："+aString+",详情："+admin.toJson().toString()+"】";
		UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
		renderTemplateJson("0", "修改成功");
	}
	
	@Before(Tx.class)
	public void initPwd() {
		int adminId = getParaToInt("adminId");
		Admin admin = Admin.dao.findById(adminId);
		admin.set("password", getPara("newPwd"));
		admin.update();
		Admin adminDB = getAdmin();
		String logInfo = "初始化帐号密码：【初始化账户“"+admin.getStr("account")+"”,详情："+admin.toJson().toString()+"】";
		UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
		renderTemplateJson("0", "修改成功");
	}
	
	public void list() {
		List<Admin> admins = Admin.dao.find("select * from rc_admin order by adminId desc");
		for (Admin admin: admins) {
			AdminRole ar = AdminRole.dao.findById(admin.getInt("roleId"));
			admin.put("role", ar);
		}
		setAttr("admins", admins);
	}
	
	@Before(Tx.class)
	public void repwd() {
		String method = getRequest().getMethod();
		if ("get".equalsIgnoreCase(method)) {
			
		} else {
			String oldpassword = getPara("oldpassword");
			String newpassword = getPara("newpassword");
			String renewpassword = getPara("renewpassword");
			
			Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_admin);
			if (admin.getStr("password").equals(oldpassword)) {
				if (!newpassword.equals(renewpassword)) {
					setPageTip("重复新密码与新密码不一至", PageTip.danger.name());
					return;
				}
				admin = Admin.dao.findById(admin.getInt("adminId"));
				admin.set("password", newpassword);
				admin.update();
				
				Admin adminDB = getAdmin();
				String logInfo = "密码修改：【账户“"+admin.getStr("account")+"”修改了密码。】";
				UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
				
				getSession().removeAttribute(RcConstants.session_login_admin);
				renderTemplateJson("0","修改成功");
			} else {
				renderTemplateJson("0","原始密码有误");
			}
			
		}
		
	}
	
	public void add() {
		List<AdminRole> roles = AdminRole.dao.find("select * from rc_adminRole");
		setAttr("roles", roles);
	}
	
	@Before(Tx.class)
	public void save() {
		Admin adminDB = getAdmin();
		Admin admin = getModel(Admin.class);
		Admin dbAdmin = Admin.dao.findFirst("select * from rc_admin where account = ?", admin.getStr("account"));
		if (admin.getInt("adminId") != null && admin.getInt("adminId") > 0) {
			if (dbAdmin != null && admin.getInt("adminId").intValue() != dbAdmin.getInt("adminId").intValue()) {
				setPageTip("账号已经存在", PageTip.danger.name());
				setAttr("admin", admin);
				add();
			} else {
				if (dbAdmin == null) {
					dbAdmin = Admin.dao.findById(admin.getInt("adminId"));
				}
				dbAdmin.set("account", admin.getStr("account"));
				dbAdmin.set("roleId", admin.getInt("roleId"));
				dbAdmin.update();
				String logInfo = "添加管理员信息被修改：【修改记录为："+dbAdmin.toJson().toString()+"】";
				UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
		
				setPageTip("账号更新成功");
			}
			redirect("/rc/admin/edit/" + admin.getInt("adminId"));
		} else {
			if (dbAdmin!= null) {
				setPageTip("账号已经存在", PageTip.danger.name());
				setAttr("admin", admin);
				add();
				renderFreeMarker("add.html");
				return;
			}
			admin.set("createDate", new Date());
			admin.set("isValid", true);
			admin.save();
			String logInfo = "添加管理员信息添加：{添加记录为："+admin.toJson().toString()+"}";
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
			setPageTip("账号添加成功");
		}
		redirect("/rc/admin/list");
	}
	
	public void edit() {
		int adminId = getParaToInt(0);
		Admin admin = Admin.dao.findById(adminId);
		setAttr("admin", admin);
		add();
		render("add.html");
	}

}
