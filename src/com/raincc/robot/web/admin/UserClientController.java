package com.raincc.robot.web.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.aop.ClearLayer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.common.RcConstants;
import com.raincc.interceptor.AdminCliInterceptor;
import com.raincc.interceptor.JdCliInterceptor;
import com.raincc.kit.CommonUtilsKit;
import com.raincc.model.Admin;
import com.raincc.robot.entity.DelaySendGoods;
import com.raincc.robot.entity.LinksMsg;
import com.raincc.robot.entity.SendGroupRecord;
import com.raincc.robot.entity.SendRecord;
import com.raincc.robot.entity.UserLogRecord;
import com.raincc.robot.entity.WxJdChannel;
import com.raincc.robot.entity.WxSendTimes;
import com.raincc.robot.entity.WxUser;
import com.raincc.robot.entity.WxUserGroup;
import com.raincc.robot.util.JsonUtils;


@ControllerBind(controllerKey="/robotAdmin/rob/userCli",viewPath="admin/robot/userCli")
@Before(AdminCliInterceptor.class)
public class UserClientController extends AdminBaseController {
		
	/**
	 * 登陆客户端接口
	 */
	@ClearInterceptor(ClearLayer.ALL)
	public void userLogIn(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			String userName = getPara("userName");
			String pasWord = getPara("pasWord");
			_log.info("userName="+userName);
			_log.info("pasWord="+pasWord);
			if(StringUtils.isBlank(userName) || StringUtils.isBlank(pasWord)){
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "帐号或密码不能为空"));
				renderNull();
				return;
			}
			Admin admin = Admin.dao.findFirst("select * from rc_admin where isValid=1 and account = ? and password = ?", userName, pasWord);
			if (admin == null) {
				_log.warn("AdminLogin::" + userName + "," + pasWord + ",FAIL");
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-2", "账号或密码错误"));
				renderNull();
				return;
			} else {
				getSession().setAttribute(RcConstants.session_login_adminCli, admin);
				admin.set("lastLoginDate", new Date());
				admin.set("lastLoginIp", CommonUtilsKit.getIpAddr(getRequest()));
				admin.update();
				_log.info("AdminLogin::" + userName + "," + admin.getStr("lastLoginIp") + ",SUC");
				JsonUtils.outPutJson(getResponse(),getTemplateJson("0", "成功"));
				renderNull();
				return;
			}
		}else {
			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
		}
		renderNull();
		return;
	}
	
	/**
	 * 获取群发时间参数接口
	 */
	public void getSendTims(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Integer adminId = getAdminCliId();
			JSONObject joJsonObject = WxSendTimes.dao.findTimes(adminId);
			JsonUtils.outPutJson(getResponse(),joJsonObject);
		}
		renderNull();
	}
	
	/**
	 * 获取推广渠道接口
	 */
	@Before(JdCliInterceptor.class)
	public void getChannelInfoList(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Integer adminId = getAdminCliId();
			Long unionId = getCliJDtemporaryId();
			_log.info("adminId="+adminId);
			_log.info("unionId="+unionId);
			if(unionId != null && unionId > 0){
				List<WxJdChannel> list = WxJdChannel.dao.findChannelList(adminId,unionId);
				Map<String, Object>map = new HashMap<String, Object>();
				map.put("data", list);
				JsonUtils.outPutJson(getResponse(),JsonKit.toJson(map));
			}else {
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请在后台登录京东"));
			}
		}
		renderNull();
	}
	
	/**
	 * 1.4获取商品接口
	 */
	@Before(JdCliInterceptor.class)
	public void getGoodsInfoList(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Long unionId = getCliJDtemporaryId();
			Integer ChannelID = getParaToInt("ChannelID");
			_log.info("unionId="+unionId+",ChannelID="+ChannelID);
			if(ChannelID != null && ChannelID > 0){
				String StartDate = getPara("StartDate");
				String EndDate = getPara("EndDate");
				_log.info("StartDate="+StartDate+",EndDate="+EndDate);
				StringBuffer buffer = new StringBuffer();
				List<Object> list = new ArrayList<Object>();
				buffer.append(" where 1 = ? ");
				list.add(1);
				buffer.append(" and link.unionId = ? ");
				list.add(unionId);
				buffer.append(" and link.jdChannelId = ? ");
				list.add(ChannelID);
				if(StringUtils.isNotBlank(StartDate) && StringUtils.isNotBlank(EndDate)){
					buffer.append(" and link.createDate between ? and ? ");
					list.add(StartDate);
					list.add(EndDate);
				}else if (StringUtils.isNotBlank(StartDate) && StringUtils.isBlank(EndDate)) {
					buffer.append(" and link.createDate between ? and NOW() ");
					list.add(StartDate);
				}
				String url = " link.linksMsgId,link.commisionRatioPc,link.commisionRatioWl,link.unionId,link.skuId,link.jdChannelId,link.GoodsPict,"
						+ " link.linkUrl,link.contents,link.goodsName,link.unitPrice,link.imgUrl,link.wlUnitPrice,link.createDate,c.spaceName ";
				List<LinksMsg> linksMsgs = LinksMsg.dao.findLinksMsg(url,buffer,list);
				Map<String, Object>map = new HashMap<String, Object>();
				map.put("data", linksMsgs);
				JsonUtils.outPutJson(getResponse(),JsonKit.toJson(map));
			}else {
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "推广渠道ID不正确"));
			}
		}
		renderNull();
	}
	
	/**
	 * 1.5商品推送记录写入接口
	 */
	public void sendGoodsRecord(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			String sendRecord = getPara("RecordList");
			String userName = getPara("UserName");
			Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_adminCli);
			Integer adminId = admin.getInt("adminId");
			userName = admin.getStr("account");
			_log.info("sendRecord="+sendRecord+",userName="+userName);
			if(StringUtils.isNotBlank(sendRecord)){
				try {
					Integer count = SendRecord.dao.saveMsg(sendRecord,userName,adminId);
					if(count > 0){
						JsonUtils.outPutJson(getResponse(),getTemplateJson("0", "记录成功"));
					}else {
						JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "记录失败"));
					}
				} catch (Exception e) {
					JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "系统有误。。。"));
				}
			}else {
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "没有获取到推送记录"));
			}
		}
		renderNull();return;
	}
	
	/**
	 * 设置商品延迟推送接口
	 */
	public void setDelaySendGoods(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			String UserName = getPara("UserName");
			String DelayInfo = getPara("DelayInfo");
			Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_adminCli);
			Integer adminId = admin.getInt("adminId");
			UserName = admin.getStr("account");
			_log.info("UserName="+UserName+",DelayInfo="+DelayInfo);
			if(StringUtils.isNotBlank(UserName) && StringUtils.isNotBlank(DelayInfo)){
				try {
					JSONObject jo = JSONObject.fromObject(DelayInfo);
					Integer count = DelaySendGoods.dao.saveDelay(jo,UserName,adminId);
					if(count > 0){
						JsonUtils.outPutJson(getResponse(),getTemplateJson("0", "成功"));
					}else {
						JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "商品延迟推送记录失败"));
					}
				} catch (Exception e) {
					JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "商品延迟推送记录失败"));
				}
			}else {
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "商品推送失败"));
			}
		}else {
			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
		}
		renderNull();
	}
	
	/**
	 * 1.7获取商品延迟推送接口
	 */
	public void getDelaySendGoods (){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_adminCli);
			Integer adminId = admin.getInt("adminId");
			String UserName = admin.getStr("account");
			_log.info("UserName="+UserName+",adminId="+adminId);
			if(StringUtils.isNotBlank(UserName) && adminId != null){
				List<DelaySendGoods> list = DelaySendGoods.dao.findDelayGoods(adminId);
				Map<String, Object>map = new HashMap<String, Object>();
				map.put("data", list);
				JsonUtils.outPutJson(getResponse(),JsonKit.toJson(map));
			}else {
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "参数错误"));
			}
		}else {
			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
		}
		renderNull();
	}
	
	/**
	 * 日志写入接口
	 */
	public void userLogRecord (){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_adminCli);
			Integer adminId = admin.getInt("adminId");
			String UserName = admin.getStr("account");
			String IPAddress = getPara("IPAddress");
			String LogTime = getPara("LogTime");
			String LogInfo = getPara("LogInfo");
			String LogType = getPara("LogType");
			_log.info("UserName="+UserName+",IPAddress="+IPAddress
					+",LogTime="+LogTime+",LogInfo="+LogInfo+",LogType="+LogType);
			if(StringUtils.isNotBlank(UserName) && StringUtils.isNotBlank(IPAddress) && 
					StringUtils.isNotBlank(LogTime) && StringUtils.isNotBlank(LogInfo) && StringUtils.isNotBlank(LogType) ){
				Integer count = UserLogRecord.dao.saveLog(UserName,IPAddress,LogTime,LogInfo,LogType,adminId);
				if(count > 0){
					JsonUtils.outPutJson(getResponse(),getTemplateJson("0", "成功"));
				}else {
					JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "日志写入失败"));
				}
			}else {
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "参数错误"));
			}
		}else {
			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
		}
		renderNull();
	}

	/**
	 * 2.0 获取微信群接口
	 */
	public void findWxUserGroup(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Integer adminId = getAdminCliId();
			List<WxUser> users = WxUser.dao.find("select Uin,wxUserId,userNickName,UserName from rb_wxuser where isValid = true and adminId = ? ",adminId);
			for (WxUser wxUser : users) {
				Long Uin = wxUser.getLong("Uin");
				List<WxUserGroup> groups = WxUserGroup.dao.find("select g.groupId,g.toUserName,g.NickName,g.base_uri,g.cookies,ch.jdChannelId ,ch.spaceName "
						+ " from rb_wxusergroup g "
						+ " left join rb_jdchannel ch on ch.channelId = g.channelId "
						+ "where g.Uin = ? and g.isValid = 2 and g.isPutaway = true ",Uin);
				for (WxUserGroup wxUserGroup : groups) {
					Integer jdChannelId = wxUserGroup.getInt("jdChannelId");
					if(jdChannelId == null){
						wxUserGroup.put("jdChannelId", 0);
					}
				}
				wxUser.put("groupData", groups);
			}
			JSONObject map = new JSONObject();
			map.put("code", "0");
			map.put("data",JsonKit.toJson(users));
			JsonUtils.outPutJson(getResponse(),map);
		}else {
			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
		}
		renderNull();
	}
	
	/**
	 * 2.1 获取自动发品的商品
	 */
	@Before(JdCliInterceptor.class)
	public void findProduct(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Long unionId = getCliJDtemporaryId();
			Integer ChannelID = getParaToInt("ChannelID");
			_log.info("unionId="+unionId+",ChannelID="+ChannelID);
			
			StringBuffer buffer = new StringBuffer();
			List<Object> list = new ArrayList<Object>();
			buffer.append(" where 1 = ? ");
			list.add(1);
			buffer.append(" and link.unionId = ? ");
			list.add(unionId);
//			if(ChannelID == null || ChannelID < 0){
//				ChannelID = 0;
//			}
			if(ChannelID != null ){
				buffer.append(" and link.jdChannelId = ? ");
				list.add(ChannelID);
			}
			
			buffer.append(" and link.isSelf = ? ");
			list.add(true);
			
			buffer.append(" and link.statusType <= ? ");
			list.add(1);
			String url = " link.linksMsgId,link.commisionRatioPc,link.commisionRatioWl,link.unionId,link.skuId,link.jdChannelId,link.GoodsPict,"
					+ " link.linkUrl,link.contents,link.goodsName,link.unitPrice,link.imgUrl,link.wlUnitPrice,link.createDate,c.spaceName ";
			List<LinksMsg> linksMsgs = LinksMsg.dao.findProductMsg(url,buffer,list);
			for (LinksMsg linksMsg : linksMsgs) {
				Integer jdChannelId = linksMsg.getInt("jdChannelId");
				if(jdChannelId == null){
					linksMsg.put("jdChannelId", 0);
				}
			}
			Map<String, Object>map = new HashMap<String, Object>();
			map.put("data", linksMsgs);
			JsonUtils.outPutJson(getResponse(),JsonKit.toJson(map));
		}else {
			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
		}
		renderNull();
	}
	
	/**
	 * 2.2  设置群发商品状态
	 */
	public void setStatus(){
		String method = getReqMethod();
		if ("post".equalsIgnoreCase(method)) {
			Integer linksMsgId = getParaToInt("linksMsgId");
			Integer groupId = getParaToInt("groupId");
			Integer statusType = getParaToInt("statusType");
			if(groupId != null && groupId > 0 && linksMsgId != null && linksMsgId > 0 && statusType != null){
				SendGroupRecord.dao.saveRecord(linksMsgId,groupId);
				Db.update("update rb_linksmsg set statusType = ? where linksMsgId = ? ",statusType,linksMsgId);
				JsonUtils.outPutJson(getResponse(),getTemplateJson("0", "成功"));
			}else {
				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "参数不能为空"));
			}
		}else {
			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
		}
		renderNull();
		
	}
	
//	/**
//	 * 2.3  记录群发送商品记录
//	 */
//	public void sendGroupRecord(){
//		String method = getReqMethod();
//		if ("post".equalsIgnoreCase(method)) {
//			Integer linksMsgId = getParaToInt("linksMsgId");
//			Integer groupId = getParaToInt("groupId");
//			if(groupId != null && groupId > 0 && linksMsgId != null && linksMsgId > 0){
//				SendGroupRecord.dao.saveRecord(linksMsgId,groupId);
//				JsonUtils.outPutJson(getResponse(),getTemplateJson("0", "成功"));
//			}else {
//				JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "参数不能"));
//			}
//		}else {
//			JsonUtils.outPutJson(getResponse(),getTemplateJson("-1", "请用POST方法"));
//		}
//		renderNull();
//	}
	
	
}
