package com.raincc.robot.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;

@SuppressWarnings("serial")
@TableBind(tableName="rb_jdpermission",pkName="id")
public class JdPermission extends BaseModel<JdPermission> {
	
	public static final JdPermission dao = new JdPermission();

}
