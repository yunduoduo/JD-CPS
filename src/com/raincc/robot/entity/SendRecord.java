package com.raincc.robot.entity;

import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName="rb_sendrecord",pkName="id")
public class SendRecord extends BaseModel<SendRecord> {
	
	public static final SendRecord dao = new SendRecord();

	@Before(Tx.class)
	public Integer saveMsg(String sendRecord, String userName, Integer adminId) {
		Integer count = -1;
		JSONArray jsonArray = JSONArray.fromObject(sendRecord);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jo = jsonArray.getJSONObject(i);
			new SendRecord()
			.set("adminId",adminId)
			.set("UserName",userName)
			.set("RecordID",jo.getString("RecordID"))
			.set("GoodsID",jo.getInt("GoodsID"))
			.set("SendWXCount",jo.getInt("SendWXCount"))
			.set("SuccessCount",jo.getInt("SuccessCount"))
			.set("SendTime",jo.getString("SendTime"))
			.set("createDate",new Date())
			.save();
		}
		count = 1;
		return count;
	}

}
