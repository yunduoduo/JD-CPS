package com.raincc.robot.entity;
import com.jfinal.ext.plugin.tablebind.TableBind;

@SuppressWarnings("serial")
@TableBind(tableName="rb_logtype",pkName="logTypeId")
public class LogType extends BaseModel<LogType> {
	
	public static final Integer LOG_SendTims 		= 1; //群发时间修改日志
	
	public static final Integer LOG_UPDATE_MSG 		= 2; //修改信息
	
	public static final Integer LOG_MANAGE_USER		= 3; //管理用户
	
	public static final LogType dao = new LogType();
	
}
