package com.raincc.robot.entity;
import java.util.Date;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName="rb_user_logs",pkName="logId")
public class UserLogs extends BaseModel<UserLogs> {

	public static final UserLogs dao = new UserLogs();
	
	@Before(Tx.class)
	public void saveLogsMsg(String logInfo,Integer logTypeId,Integer adminId,
			String userName,String IPAddress){
		new UserLogs()
		.set("adminId",adminId)
		.set("userName",userName)
		.set("IPAddress",IPAddress)
		.set("logTypeId",logTypeId)
		.set("logInfo",logInfo)
		.set("createDate",new Date())
		.save();
	}
}
