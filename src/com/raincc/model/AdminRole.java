package com.raincc.model;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.kit.CommonUtilsKit;
import com.raincc.robot.entity.BaseModel;
import com.raincc.robot.entity.LogType;
import com.raincc.robot.entity.UserLogs;

@TableBind(tableName = "rc_adminRole", pkName = "roleId")
public class AdminRole extends BaseModel<AdminRole> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final AdminRole dao = new AdminRole();

	/**
	 * 刷新权限
	 * @param groupId
	 * @param permissionIds
	 */
	@Before(Tx.class)
	public void refreshPermission(int roleId, Integer[] permissionIds,Admin adminDB,String IPAddress) {
		List<Integer> idList = new ArrayList<Integer>();
		AdminRole role = AdminRole.dao.findById(roleId);
		if (permissionIds != null)
		for (Integer id : permissionIds) {
			idList.add(id);
		}
		List<Integer> exists = new ArrayList<Integer>();
		List<RolePermissionRelation> rs = RolePermissionRelation.dao.find("select * from rc_rolePermissionRelation where roleId = ?", roleId);
		List<String> dList = new ArrayList<String>();
		for (RolePermissionRelation r : rs) {
			if (idList.contains(r.getInt("permissionId"))) {
				exists.add(r.getInt("permissionId"));
			} else {
				Permission p = Permission.dao.findFirst("select permissionName from rc_permission where permissionId = ? ",r.getInt("permissionId"));
				if(p != null){
					dList.add(p.getStr("permissionName"));
				}
				r.delete();
			}
		}
		if(dList.size() > 0){
			String logInfo = "角色修改权限：【角色“"+role.getStr("roleName")+"”删除："+dList.toString()+" 的权限。】";
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), IPAddress);
		}
		idList.removeAll(exists);
		List<String> nameList = new ArrayList<String>();
		for (Integer id : idList) {
			RolePermissionRelation r = new RolePermissionRelation();
			r.set("roleId", roleId);
			r.set("permissionId", id);
			r.save();
			Permission p = Permission.dao.findFirst("select permissionName from rc_permission where permissionId = ? ",id);
			if(p != null){
				nameList.add(p.getStr("permissionName"));
			}
		}
		if(nameList.size() > 0){
			String logInfo = "角色修改权限：{角色“"+role.getStr("roleName")+"”添加："+nameList.toString()+" 的权限。}";
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_MANAGE_USER, adminDB.getInt("adminId"), adminDB.getStr("account"), IPAddress);
		}
	}
	
}
