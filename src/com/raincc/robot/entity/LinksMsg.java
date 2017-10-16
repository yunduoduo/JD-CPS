package com.raincc.robot.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.jd.core.jdutils.TUtils;

@SuppressWarnings("serial")
@TableBind(tableName="rb_linksmsg",pkName="linksMsgId")
public class LinksMsg extends BaseModel<LinksMsg> {

	public static final LinksMsg dao = new LinksMsg();

	/**转链接记录
	 * @param mId
	 * @param value
	 * @param unionId
	 * @param jdChannelId
	 * @param skuId
	 * @param adminId
	 */
	@Before(Tx.class)
	public void saveLikesMsg(String access_token,Integer mId, String value,Long unionId,Integer jdChannelId,String skuId,Integer adminId,boolean bo) {
		ListenerMsg aListenerMsg = ListenerMsg.dao.findById(mId);
		if(aListenerMsg != null){
			aListenerMsg.set("isLinks",true);
			aListenerMsg.update();
			String content = aListenerMsg.getStr("content");
			LinksMsg msg = new LinksMsg();
			msg.set("listenerMsgId",mId);
			msg.set("unionId",unionId);
			msg.set("skuId",skuId);
			msg.set("adminId",adminId);
			msg.set("jdChannelId",jdChannelId == null ? 0 : jdChannelId);
			msg.set("contents",content.replace(skuId, value).replace("sku:", "").replace("skuId:", "").replace("sku：", "").replace("skuId：", ""));
			msg.set("linkUrl",value);
			msg.set("GoodsPict",aListenerMsg.getStr("imgUrl"));
			msg.set("isSelf",bo);
			msg.set("createDate",new Date());
			msg.save();
			getGoosInfo(access_token,msg.getInt("linksMsgId"),skuId);
		}
	}

	/**
	 * {"jingdong_service_promotion_goodsInfo_responce":{"code":"0","
	 * getpromotioninfo_result":
	 * "{\"message\":\"接口成功\",\"result\":[],\"sucessed\":true}"}}
	 * 
	 * 
	 * {"jingdong_service_promotion_goodsInfo_responce":
	 * 		{"code":"0","getpromotioninfo_result":"
	 * 						{\"message\":\"接口成功\",\"result\":
		 * 						[ {\"commisionRatioPc\":80.00,
		 * 						\"commisionRatioWl\":80.00,
		  						\"endDate\":32472115200000,
		  						\"goodsName\":\"发雅丝（FAYASI）免洗护发精油 卷发免洗头发精油发膜专用修护干枯直发毛躁护发素 橙花精油 70ML\",
		  						\"imgUrl\":\"http://img14.360buyimg.com/n1/jfs/t4327/312/161860728/178265/3d8689a5/58b01d2cN27dd9277.jpg\",
		  						\"materialUrl\":\"http://item.jd.com/12485168576.html\",
		  						\"shopId\":202969,
		  						\"skuId\":12485168576,
		  						\"startDate\":1500566400000,
		  						\"unitPrice\":22.00,
		  						\"wlUnitPrice\":22.00} ],
		  						\"sucessed\":true}"
	  						}
	  						}
	 * @param access_token
	 * @param linksMsgId
	 * @param skuIds
	 */
	private void getGoosInfo(final String access_token,final Integer linksMsgId,final String skuIds) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jo = new JSONObject();
				jo.put("skuIds",skuIds);
				String body = TUtils.getJdInterface(access_token, TConstants.JD_METHOD_getGoodsInfo, jo);
				if(StringUtils.isNotBlank(body) && !body.contains("error_response")){
					JSONObject jsonObject = JSONObject.fromObject(body);
					JSONObject j = jsonObject.getJSONObject("jingdong_service_promotion_goodsInfo_responce");
					JSONArray array = j.getJSONObject("getpromotioninfo_result").getJSONArray("result");
					if(array.size() > 0){
						JSONObject json = array.getJSONObject(0);
						String goodsName = json.getString("goodsName");	
						Double unitPrice = json.getDouble("unitPrice");
						Double wlUnitPrice = json.getDouble("wlUnitPrice");
						String imgUrl = json.getString("imgUrl");
						Double commisionRatioPc = json.getDouble("commisionRatioPc");
						Double commisionRatioWl = json.getDouble("commisionRatioWl");
						Integer shopId = json.getInt("shopId");
						String materialUrl = json.getString("materialUrl");
						Db.update("update rb_linksmsg set goodsName = ? ,unitPrice = ? ,wlUnitPrice = ? ,imgUrl = ? ,commisionRatioPc = ? ,commisionRatioWl = ?,"
								+ " shopId = ? ,materialUrl = ? where linksMsgId = ?  "
								,goodsName,unitPrice,wlUnitPrice,imgUrl,commisionRatioPc,commisionRatioWl,shopId,materialUrl,linksMsgId);
					}
					
				}
			}
		},"LinksMsg-getGoosInfo-thread").start();
		
		
		
	}

	public List<LinksMsg> findLinksMsg(String url, StringBuffer buffer, List<Object> list) {
		return find("select "+url+" from rb_linksmsg link left join rb_jdchannel c "
				+ " on c.jdChannelId = link.jdChannelId "+
				buffer.toString(),list.toArray());
	}
	
	public List<LinksMsg> findProductMsg(String url, StringBuffer buffer, List<Object> list) {
		return find("select "+url+" from rb_linksmsg link left join rb_jdchannel c "
				+ " on c.jdChannelId = link.jdChannelId "+
				buffer.toString(),list.toArray());
	}



}
