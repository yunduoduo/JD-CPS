package com.raincc.model;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.raincc.robot.entity.BaseModel;

@TableBind(tableName = "rc_admin", pkName = "adminId")
public class Admin extends BaseModel<Admin> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Admin dao = new Admin();
	
}
