package com.raincc.kit;

import java.util.ArrayList;
import java.util.List;

import com.raincc.model.Permission;

public class PermissionKit {
	
	private static List<String> permissions;
	
	public static List<String> getPermission() {
		if (permissions == null) {
			init();
		}
		return permissions;
	}
	
	public static void clear() {
		synchronized(PermissionKit.class) {
			permissions = null;
		}
	}

	private static void init() {
		synchronized(PermissionKit.class) {
			permissions = new ArrayList<String>();
			
			List<Permission> ps = Permission.dao.find("select * from rc_permission");
			for (Permission p : ps) {
				permissions.add(p.getStr("actionUrl"));
			}
		}
	}

}
