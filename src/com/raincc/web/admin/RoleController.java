package com.raincc.web.admin;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.kit.CommonUtilsKit;
import com.raincc.model.Admin;
import com.raincc.model.AdminRole;
import com.raincc.model.Permission;
import com.raincc.robot.entity.LogType;
import com.raincc.robot.entity.UserLogs;

@ControllerBind(controllerKey = "/rc/admin/role")
@Before(AdminInterceptor.class)
public class RoleController extends AdminBaseController {
	
	public void list() {
		int pageNo = getParaToInt("pageNo", 1);
		Page<AdminRole> page = AdminRole.dao.paginate(pageNo, 20, "select *", "from rc_adminRole order by roleId desc");
		for (AdminRole role : page.getList()) {
			int roleId = role.getInt("roleId");
			List<Permission> ps = Permission.dao.find("select p.* from rc_permission p, rc_rolePermissionRelation r where p.permissionId = r.permissionId and r.roleId = ?", roleId);
			role.put("permissions", ps);
		}
		
		setAttr("page", page);
	}
	
	public void add() {
	}
	
	
	@Before(Tx.class)
	public void save() {
		AdminRole ag = getModel(AdminRole.class, "role");
		Admin adminDB = getAdmin();
		if (ag.getInt("roleId") != null && ag.getInt("roleId") > 0) {
			ag.update();
			String logInfo = "管理员组更新：【更新详情："+ag.toJson().toString()+"】";
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
			setPageTip("管理员组更新成功");
			redirect("/rc/admin/role/edit/" + ag.getInt("roleId"));
		} else {
			ag.set("isRoot", false);
			ag.save();
			String logInfo = "管理员组添加：{添加详情："+ag.toJson().toString()+"}";
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
			setPageTip("管理员组添加成功");
			redirect("/rc/admin/role/add");
		}
	}
	
	public void edit() {
		int roleId = getParaToInt(0);
		AdminRole role = AdminRole.dao.findById(roleId);
		setAttr("role", role);
		render("add.html");
	}
	
	public void save_permission() {
		int roleId = getParaToInt("roleId", 0);
		Integer[] permissionIds = getParaValuesToInt("permissionId");
		AdminRole.dao.refreshPermission(roleId, permissionIds,getAdmin(),CommonUtilsKit.getIpAddr(getRequest()));
	}

}
