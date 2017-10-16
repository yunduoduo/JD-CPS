package com.raincc.robot.entity;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName="rb_sendgrouprecord",pkName="sendId")
public class SendGroupRecord extends BaseModel<SendGroupRecord> {

	public static final SendGroupRecord dao = new SendGroupRecord();

	@Before(Tx.class)
	public void saveRecord(Integer linksMsgId, Integer groupId) {
		new SendGroupRecord()
		.set("linksMsgId",linksMsgId )
		.set("groupId", groupId)
		.set("createDate", new Date())
		.save();
	}
}
