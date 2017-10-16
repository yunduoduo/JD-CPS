package com.raincc.model;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.raincc.robot.entity.BaseModel;

@TableBind(tableName = "rc_rolePermissionRelation", pkName = "relationId")
public class RolePermissionRelation extends BaseModel<RolePermissionRelation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final RolePermissionRelation dao = new RolePermissionRelation();
	
}
