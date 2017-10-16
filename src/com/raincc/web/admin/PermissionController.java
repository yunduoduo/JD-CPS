package com.raincc.web.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.kit.CommonUtilsKit;
import com.raincc.kit.PermissionKit;
import com.raincc.model.Admin;
import com.raincc.model.GroupPermission;
import com.raincc.model.Permission;
import com.raincc.robot.entity.LogType;
import com.raincc.robot.entity.UserLogs;

@ControllerBind(controllerKey = "/rc/admin/permission")
@Before(AdminInterceptor.class)
public class PermissionController extends AdminBaseController {
	
	public void list() {
		int pageNo = getParaToInt(0, 1);
		Page<Permission> page = Permission.dao.paginate(pageNo, 20, "select *", "from rc_permission order by permissionId desc");
		setAttr("page", page);
	}
	
	@Before(Tx.class)
	public void save() {
		Admin adminDB = getAdmin();
		Permission permission = getModel(Permission.class,"p");
		if (permission.getInt("permissionId") != null && permission.getInt("permissionId") > 0) {
			permission.update();
			setPageTip("权限更新成功");
			String logInfo = "权限配置：【权限更新："+permission.toJson().toString()+"】";
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
			redirect("/rc/admin/permission/edit/" + permission.getInt("permissionId"));
		} else {
			permission.save();
			String logInfo = "权限配置：{权限添加："+permission.toJson().toString()+"}";
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
			setPageTip("权限添加成功");
			redirect("/rc/admin/permission/edit/" + permission.getInt("permissionId"));
		}
		PermissionKit.clear();
	}
	@Before(Tx.class)
	public void delete() {
		int pId = getParaToInt(0);
		Admin adminDB = getAdmin();
		Permission permission = Permission.dao.findById(pId);
		String logInfo = "权限配置：【权限删除："+permission.toJson().toString()+"】";
		UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
		permission.delete();
		setPageTip("权限删除成功");
		redirect("/rc/admin/permission/list/1");
	}
	public void add() {
	}
	public void edit() {
		int pId = getParaToInt(0);
		Permission permission = Permission.dao.findById(pId);
		setAttr("p", permission);
		
	}
	
	public void loadPermission() {
		int roleId = getParaToInt("roleId", 0);
		setAttr("roleId", roleId);
		
		List<Permission> permissions = Permission.dao.find("select * from rc_permission order by permissionId desc");
		if (roleId > 0) {
			List<Permission> relations = Permission.dao.find("select * from rc_permission p, rc_rolePermissionRelation r where p.permissionId = r.permissionId and r.roleId = ?", roleId);
			Map<Integer, Permission> mapRelations = new HashMap<Integer, Permission>();
			for (Permission relation : relations) {
				mapRelations.put(relation.getInt("permissionId"), relation);
			}
			
			for (Permission permission : permissions) {
				if (mapRelations.containsKey(permission.getInt("permissionId"))) {
					permission.put("has", true);
				} else {
					permission.put("has", false);
				}
			}
		}
		
		setAttr("permissions", permissions);
	}

}
