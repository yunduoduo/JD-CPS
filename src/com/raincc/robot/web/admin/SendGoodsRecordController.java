package com.raincc.robot.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.robot.entity.SendRecord;
import com.raincc.robot.entity.UserLogRecord;
import com.raincc.robot.entity.WxJdChannel;

@ControllerBind(controllerKey="/robotAdmin/rob/record",viewPath="admin/robot/record")
@Before(AdminInterceptor.class)
public class SendGoodsRecordController extends AdminBaseController {

	/**
	 * 商品推送记录
	 */
	public void sendRecord(){
		Integer pageNo = getParaToInt(0,1);
		StringBuffer buffer = new StringBuffer();
		buffer.append(" where 1 = ? ");
		List<Object> list = new ArrayList<Object>();
		list.add(1);
		
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate)){
			buffer.append(" and s.SendTime between ? and ? ");
			list.add(startRegDate);
			list.add(endRegDate);
		}
		String UserName = getPara("UserName");
		if(StringUtils.isNotBlank(UserName)){
			buffer.append(" and s.UserName like ? ");
			list.add("%"+UserName+"%");
		}
		String sendUrl = " s.id,s.UserName,s.RecordID,s.GoodsID,s.SendWXCount, "
				+ "s.SuccessCount, s.SendTime,s.createDate ,m.unionId,m.skuId,m.jdChannelId,m.goodsName ";
		Page<SendRecord> page = SendRecord.dao.paginate(pageNo, 20, "select "+sendUrl,
				" from rb_sendrecord s "
				+ " left join rb_linksmsg m on m.linksMsgId = s.GoodsID "+buffer.toString()
				+ " order by s.id desc ",list.toArray());
		for (SendRecord send : page.getList()) {
			Integer jdChannelId = send.getInt("jdChannelId");
			WxJdChannel channel = WxJdChannel.dao.findFirst("select spaceName from rb_jdchannel where jdChannelId = ? ",jdChannelId);
			if(channel == null){
				send.put("spaceName","");
			}else {
				send.put("spaceName",channel.getStr("spaceName"));
			}
		}
		setAttr("page", page);
	}
	
	/**
	 * 客户端操作日志
	 */
	public void userLogRecord(){
		Integer pageNo = getParaToInt(0,1);
		StringBuffer buffer = new StringBuffer();
		buffer.append(" where 1 = ? ");
		List<Object> list = new ArrayList<Object>();
		list.add(1);
		String UserName = getPara("UserName");
		if(StringUtils.isNotBlank(UserName)){
			buffer.append("and lo.UserName like ? ");
			list.add("%"+UserName+"%");
		}
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate)){
			buffer.append(" and lo.LogTime between ? and ? ");
			list.add(startRegDate);
			list.add(endRegDate);
		}
		String url = " lo.logRecordId,lo.adminId,lo.UserName,lo.IPAddress,"
				+ " lo.LogTime,lo.LogInfo,lo.LogType ";
		Page<UserLogRecord> page = UserLogRecord.dao.paginate(pageNo, 20,"select "+url,
				" from rb_user_log_record lo "+buffer.toString()+
				" order by lo.logRecordId desc ",list.toArray());
		setAttr("page",page);
		
	}
}
