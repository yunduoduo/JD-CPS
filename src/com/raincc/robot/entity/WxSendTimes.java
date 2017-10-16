package com.raincc.robot.entity;

import java.util.Date;

import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName="rb_wxsendtimes",pkName="id")
public class WxSendTimes extends BaseModel<WxSendTimes> {

	public static final WxSendTimes dao = new WxSendTimes();

	public JSONObject findTimes(Integer adminId) {
		JSONObject j = new JSONObject();
		WxSendTimes times = WxSendTimes.dao.findFirst("select id,GoodsSendTims,PicSendTims,WXSendTimes from rb_wxsendtimes where adminId = ? ",adminId);
		if(times != null){
			j.put("GoodsSendTims",times.getLong("GoodsSendTims"));
			j.put("PicSendTims",times.getLong("PicSendTims"));
			j.put("WXSendTimes",times.getLong("WXSendTimes"));
			j.put("code", 0);
		}
		return j;
	}


	/**设置群发时间
	 * @param adminId
	 * @param userName
	 * @param times
	 * @param IPAddress
	 */
	@Before(Tx.class)
	public void saveTimes(Integer adminId, String userName,WxSendTimes times,String IPAddress) {
		if(times.enableSave()){
			times.set("createDate",new Date());
		}
		times.set("adminId", adminId);
		times.set("upTime",new Date());
		times.saveOrUpdate();
		String logInfo = "群发时间更改：【商品发送时间间隔:"+times.getLong("GoodsSendTims")
				+",商品图片发送时间间隔:"+times.getLong("PicSendTims")
				+",微信群发送时间间隔:"+times.getLong("WXSendTimes")+"】";
		_log.info(logInfo+",更改用户为："+userName);
		UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_SendTims, adminId, userName, IPAddress);
	}
}
