package com.raincc.robot.entity;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName="rb_delay_send_goods",pkName="delayGoodsId")
public class DelaySendGoods extends BaseModel<DelaySendGoods> {

	public static final DelaySendGoods dao = new DelaySendGoods();

	@Before(Tx.class)
	public Integer saveDelay(JSONObject jo, String userName,Integer adminId) {
		Integer count = -1;
		new DelaySendGoods()
		.set("adminId",adminId)
		.set("UserName",userName)
		.set("DelayID",jo.getString("DelayID"))
		.set("GoodsID",jo.getInt("GoodsID"))
		.set("DelayTime",jo.getString("DelayTime"))
		.set("createDate",new Date())
		.save();
		count = 1;
		return count;
	}

	public List<DelaySendGoods> findDelayGoods(Integer adminId) {
		String url = " d.delayGoodsId,d.DelayID,d.DelayTime,link.linksMsgId,link.commisionRatioPc,link.commisionRatioWl,link.unionId,link.skuId,link.jdChannelId,link.GoodsPict,"
				+ " link.linkUrl,link.contents,link.goodsName,link.unitPrice,link.imgUrl,link.wlUnitPrice,link.createDate ";
		List<DelaySendGoods> list = find("select "+url+" from rb_delay_send_goods d "
				+ " ,rb_linksmsg link where d.GoodsID = link.linksMsgId and d.isValid = true ");
		return list;
	}
}
