package com.raincc.robot.entity;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName="rb_jdchannel",pkName="channelId")
public class WxJdChannel extends BaseModel<WxJdChannel> {

	public static final WxJdChannel dao = new WxJdChannel();

	@Before(Tx.class)
	public void saveChannel(JSONObject jo,Integer adminId) {
		WxJdChannel cha = new WxJdChannel().findFirst("select channelId from rb_jdchannel where jdChannelId = ? and unionId = ? ",jo.getInteger("id"),jo.getInteger("unionId"));
		if(cha == null ){
			cha = new WxJdChannel();
		}
		cha.set("jdChannelId",jo.getInteger("id"));
		cha.set("unionId",jo.getInteger("unionId"));
		cha.set("spaceName",jo.getString("spaceName"));
		String createTime = jo.getLongValue("createTime") +"";
        Date date = new Date(new Long(createTime));
//        String jdCreateTime = TConstants.simpleDateFormat.format(date);
		cha.set("jdCreateTime",date);
		cha.set("clickCount",jo.getInteger("clickCount"));
		cha.set("orderCount",jo.getInteger("orderCount"));
		cha.set("orderPrice",jo.getDouble("orderPrice"));
		cha.set("orderCommission",jo.getDouble("orderCommission"));
		cha.set("createDate",new Date());
		cha.saveOrUpdate();
		
	}

	public List<WxJdChannel> findChannelList(Integer adminId, Long unionId) {
		return find("select channelId,unionId,spaceName,jdChannelId from rb_jdchannel where unionId = ? ",unionId);
	}
}
