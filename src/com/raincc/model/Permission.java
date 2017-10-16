package com.raincc.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

@TableBind(tableName = "rc_permission", pkName = "permissionId")
public class Permission extends Model<Permission> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Permission dao = new Permission();

	/**
	 * 获取用户权限
	 * @param groupId
	 * @return
	 */
	public Map<String, Permission> findPermission(Integer groupId) {
		List<Permission> ps = this.find("select p.* from rc_permission p, rc_groupPermission gp where gp.permissionId = p.permissionId and gp.groupId = ?", groupId);
		Map<String, Permission> pMap = new HashMap<String, Permission>();
		for (Permission p : ps) {
			pMap.put(p.getStr("actionUrl"), p);
		}
		return pMap;
	}

}
