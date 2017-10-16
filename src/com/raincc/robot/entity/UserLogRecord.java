package com.raincc.robot.entity;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName="rb_user_log_record",pkName="logRecordId")
public class UserLogRecord extends BaseModel<UserLogRecord> {
	
	public static final UserLogRecord dao = new UserLogRecord();

	@Before(Tx.class)
	public Integer saveLog(String userName, String iPAddress, String logTime,
			String logInfo, String logType, Integer adminId) {
		Integer count = -1;
		new UserLogRecord()
		.set("adminId",adminId)
		.set("UserName",userName)
		.set("IPAddress",iPAddress)
		.set("LogTime",logTime)
		.set("LogInfo",logInfo)
		.set("LogType",logType)
		.set("createDate",new Date())
		.save();
		count = 1;
		return count;
	}

}
