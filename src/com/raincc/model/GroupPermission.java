package com.raincc.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;

//@TableBind(tableName = "rc_groupPermission", pkName = "gpId")
public class GroupPermission extends Model<GroupPermission> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final GroupPermission dao = new GroupPermission();

	/**
	 * 刷新权限
	 * @param groupId
	 * @param permissionIds
	 */
	@Before(Tx.class)
	public void refresh(int groupId, Integer[] permissionIds) {
		List<Integer> idList = new ArrayList<Integer>();
		if (permissionIds != null)
		for (Integer id : permissionIds) {
			idList.add(id);
		}
		
		List<Integer> exists = new ArrayList<Integer>();
		List<GroupPermission> ps = GroupPermission.dao.find("select * from rc_groupPermission where groupId = ?", groupId);
		for (GroupPermission p : ps) {
			if (idList.contains(p.getInt("permissionId"))) {
				exists.add(p.getInt("permissionId"));
			} else {
				// delete
				p.delete();
			}
		}
		idList.removeAll(exists);
		
		for (Integer id : idList) {
			GroupPermission p = new GroupPermission();
			p.set("groupId", groupId);
			p.set("permissionId", id);
			p.save();
		}
	}

}
