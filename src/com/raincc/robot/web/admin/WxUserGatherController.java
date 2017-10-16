package com.raincc.robot.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.interceptor.JdLoginInterceptor;
import com.raincc.kit.CommonUtilsKit;
import com.raincc.model.Admin;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.LinksMsg;
import com.raincc.robot.entity.ListenerMsg;
import com.raincc.robot.entity.LogType;
import com.raincc.robot.entity.SendGroupRecord;
import com.raincc.robot.entity.UserLogs;
import com.raincc.robot.entity.WxJdChannel;
import com.raincc.robot.entity.WxUser;
import com.raincc.robot.entity.WxWechatMeta;
import com.raincc.robot.jd.core.jdutils.TUtils;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;
import com.raincc.robot.me.biezhi.wechat.service.WechatService;
import com.raincc.robot.me.biezhi.wechat.service.WechatServiceImpl;
import com.raincc.robot.me.biezhi.wechat.util.Matchers;

/**用户采集
 * @author Administrator
 *
 */
@ControllerBind(controllerKey="/robotAdmin/rob/gather",viewPath="admin/robot/gather")
@Before(AdminInterceptor.class)
public class WxUserGatherController extends AdminBaseController {

	
	protected static final Logger _log = Logger.getLogger(WxUserGatherController.class);
	int playWeChat = 0;
	/**
	 * 群信息采集
	 */
	@Before(Tx.class)
	public void index(){
		String ids = getPara("ids");
		String[] id = ids.split("&");
		Integer uin = getParaToInt("uin");
		Integer count = 0;
		Db.update("update rb_wxusergroup set isValid = 0 where Uin = ? and isValid = 1 ",uin);
		if(StringUtils.isBlank(ids)){
			renderTemplateJson("0","已经取消所有采集群");
			return;
		}
		for (String groupId : id) {
			Integer gd = Integer.parseInt(groupId);
			count ++;
			Db.update("update rb_wxusergroup set isValid = 1,channelId = 0 where groupId = ? ",gd);
		}
		if(count > 0){
			renderTemplateJson("0","正在监听群消息。。。");
		}else {
			renderTemplateJson("-1","监听群消息失败。。。");
		}
	}
	
	/**
	 * 监听
	 */
	public void listenerMsg(){
		Integer uin = getParaToInt("uin");
		WxWechatMeta wxWechatMeta = WxWechatMeta.dao.findFirst("select * from rb_wxwechatmeta where Uin = ? ",uin);
		if(wxWechatMeta == null){
			renderTemplateJson("-1","没有获取到监听的时间");
			return;
		}
		WechatMeta wMeta = Matchers.wxWeToWech(wxWechatMeta);
		Integer adminId = getAdminId();
		start(new WechatServiceImpl(), wMeta,adminId);
		renderTemplateJson("0","开始监听。。。");
	}
	
	public void start(final WechatService wechatService, final WechatMeta wechatMeta,final Integer adminId){
		 new Thread(new Runnable() {
			@Override
			public void run() {
					_log.info("进入消息监听模式 ...");
					try {
						wechatService.choiceSyncLine(wechatMeta);
					} catch (Exception e) {
						_log.info("监听心跳失败");
						return ;
					}
					Long Uin = wechatMeta.getUser().getLong("Uin");
					while(true){
						int[] arr = wechatService.syncCheck(wechatMeta);
						System.err.println("当前用户登录用户为：UserName="+wechatMeta.getUser().getString("UserName")+",用户NickName="+wechatMeta.getUser().getString("NickName")+",署名为Signature="+wechatMeta.getUser().getString("Signature"));
						_log.info("retcode={"+arr[0]+"}, selector={"+arr[1]+"}");
						
						if(arr[0] == 1100 || arr[0] == 1101){
							_log.info("你在手机上登出了微信，再见！"+wechatMeta.getUser().getString("UserName"));
//							Db.update("update rb_wxusergroup set isValid = 0,isPutaway = false where Uin = ? ",Uin);
//							Db.update("update rb_wxuser set isValid = false where Uin = ? ",wechatMeta.getUser().getString("Uin"));
							break;
						}
						if(arr[0] == 0){
							if(arr[1] == 2){
								JSONObject data = wechatService.webwxsync(wechatMeta);
								wechatService.handleMsg(wechatMeta, data,adminId,Uin);
							} else if(arr[1] == 6){
								JSONObject data = wechatService.webwxsync(wechatMeta);
								wechatService.handleMsg(wechatMeta, data,adminId,Uin);
							} else if(arr[1] == 7){
								playWeChat += 1;
								_log.info("你在手机上玩微信被我发现了 {"+playWeChat+"} 次");
								wechatService.webwxsync(wechatMeta);
							} else if(arr[1] == 3){
								continue;
							} else if(arr[1] == 0){
								continue;
							}
						} else {
							// 
						}
					
				}
				System.out.println("stopstop");
				
			}
			
		}, "wechat-listener-thread").start();
	}
	
	/**
	 * 采集消息列表
	 */
	@Before(JdLoginInterceptor.class)
	public void gatherList(){
		Integer pageNo = getParaToInt(0,1);
		Long unionId = getJDUnionId();
		Integer adminId  = getAdminId();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" where 1= ? ");
		List<Object>list = new ArrayList<Object>();
		list.add(1);
		buffer.append(" and m.adminId = ? ");
		list.add(adminId);
		
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate)){
			buffer.append(" and m.createDate between ? and ? ");
			list.add(startRegDate);
			list.add(endRegDate);
		}
		Page<ListenerMsg> page = ListenerMsg.dao.paginate(pageNo, 50, "select m.listenerMsgId,m.isLinks,c.channelId,c.jdChannelId,c.spaceName,m.Uin,m.NickName,m.FromUserName,m.GroupName,m.content,m.imgUrl,m.createDate,m.skuId ", 
				" from  rb_listener_msg  m "+
				" left join rb_jdchannel c on c.channelId = m.channelId "+buffer.toString()+
				"  order by listenerMsgId desc ",list.toArray());
		for (ListenerMsg wx : page.getList()) {
			String aString = wx.getStr("GroupName");
			String NickName = wx.getStr("NickName");
			NickName = NickName.replace("<", "&lt;").replace(">", "&gt;");
			aString = aString.replace("<", "&lt;").replace(">", "&gt;");
			wx.put("gName", aString );
			wx.put("NName", NickName);
		}
		setAttr("page", page);
		
		List<WxJdChannel> channel = WxJdChannel.dao.find("select channelId,jdChannelId,spaceName from rb_jdchannel where unionId = ? ",unionId);
		setAttr("channel",channel);
		
	}
	
	public void edit(){
		Integer idInteger = getParaToInt(0);
		ListenerMsg msg = ListenerMsg.dao.findById(idInteger);
		setAttr("ca",msg);
	}
	public void editLinks(){
		Integer idInteger = getParaToInt(0);
		setAttr("ca",LinksMsg.dao.findFirst("select linksMsgId,contents,imgUrl,GoodsPict from rb_linksmsg where linksMsgId = ? ",idInteger));
	}
	@Before(Tx.class)
	public void saveMsg(){
		ListenerMsg aListenerMsg = getModel(ListenerMsg.class, "ca");
		if(!aListenerMsg.getStr("imgUrl").contains(TConstants.PJECT_URL)){
			aListenerMsg.set("imgUrl", TConstants.PJECT_URL+aListenerMsg.getStr("imgUrl"));
		}
		aListenerMsg.update();
		ListenerMsg msg = ListenerMsg.dao.findById(aListenerMsg.getInt("listenerMsgId"));
		String logInfo = "监听信息被修改：【主键："+msg.getInt("listenerMsgId")+",用户Uin："+msg.getStr("Uin")+",用户昵称："+msg.getStr("NickName")
				+",skuId："+msg.getLong("skuId")
				+"】";
		Admin admin = getAdmin();
		UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_UPDATE_MSG, admin.getInt("adminId"), admin.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
		
		setPageTip("保存成功");
		redirect("/robotAdmin/rob/gather/edit/"+aListenerMsg.getInt("listenerMsgId"));
	}
	@Before(Tx.class)
	public void saveLinksMsg(){
		LinksMsg msg = getModel(LinksMsg.class, "ca");
		if(!msg.getStr("GoodsPict").contains(TConstants.PJECT_URL)){
			msg.set("GoodsPict", TConstants.PJECT_URL+msg.getStr("GoodsPict"));
		}
		msg.update();
		LinksMsg msg2 = LinksMsg.dao.findById(msg.getInt("linksMsgId"));
		String logInfo = "转链后的信息被修改：【主键："+msg2.getInt("linksMsgId")+",京东联盟ID："+msg2.getLong("unionId")
				+",skuId:"+msg2.getLong("skuId")
				+",商品名称："+msg2.getStr("goodsName")+"】";
		Admin admin = getAdmin();
		UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_UPDATE_MSG, admin.getInt("adminId"), admin.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
		
		setPageTip("保存成功");
		redirect("/robotAdmin/rob/gather/editLinks/"+msg.getInt("linksMsgId"));
	}
	public void deleteMsg(){
		Integer lId = getParaToInt("lId");
		ListenerMsg msg = ListenerMsg.dao.findById(lId);
		if(msg != null){
			String logInfo = "监听信息被删除：【主键："+msg.getInt("listenerMsgId")+",用户Uin："+msg.getStr("Uin")+",用户昵称："+msg.getStr("NickName")
					+",skuId："+msg.getLong("skuId")
					+"】";
			Admin admin = getAdmin();
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_UPDATE_MSG, admin.getInt("adminId"), admin.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
			Db.update("delete from rb_listener_msg where listenerMsgId = ? ",lId);
			renderTemplateJson("0", "删除成功");
		}else {
			renderTemplateJson("-1", "删除失败");
		}
	}
	
	/**
	 * 转链接
	 */
	@SuppressWarnings("rawtypes")
	public void links(){
		String ids  = getPara("ids");
		Integer jdChannelId = getParaToInt("jdChannelId");
		Long unionId = getJDUser().getLong("unionId");//联盟Id
		if(unionId==null || unionId <= 0){
			renderTemplateJson("-1","没有获取到主键");
			return;
		}
		String access_token = TUtils.getJdToken(this);
		if(StringUtils.isBlank(access_token)){
			renderTemplateJson("-1","没有获取到权限。。。");
			return;
		}
		JSONArray jsonArray = JSONArray.fromObject(ids);
		String skuIds = "";
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < jsonArray.size(); i++) {
			net.sf.json.JSONObject j = jsonArray.getJSONObject(i);
			map.put(j.getString("skuId"),j.getInt("msgId"));
			skuIds += ","+j.getString("skuId");
		}
		skuIds = skuIds.substring(1);
		net.sf.json.JSONObject netJoson = new net.sf.json.JSONObject();
//		netJoson.put("jdUnionId",unionId);
		netJoson.put("materialIds",skuIds);
		netJoson.put("proCont",1);
		netJoson.put("positionId",jdChannelId);
		netJoson.put("subUnionId",unionId);
		
		String body = TUtils.getJdParams(unionId,access_token, TConstants.JD_METHOD_getCodeBySubUnionId, netJoson);
		System.err.println(body);
		if(body.contains("error_response") || StringUtils.isBlank(body)){
			renderTemplateJson("-1",body);
			return;
		}
		net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(body);
		net.sf.json.JSONObject j = jsonObject.getJSONObject("jingdong_service_promotion_wxsq_getCodeBySubUnionId_responce");
		net.sf.json.JSONObject a = j.getJSONObject("getcodebysubunionid_result");
		String resultCode = a.getString("resultCode");
		String resultMessage = a.getString("resultMessage");
		if("0".equals(resultCode) && resultMessage.contains("成功")){
			_log.info("resultCode="+resultCode+",resultMessage="+resultMessage);
			net.sf.json.JSONObject urlList = a.getJSONObject("urlList");
		       Iterator iterator = urlList.keys();
		       while(iterator.hasNext()){
	              String key = (String) iterator.next();
	              String value = urlList.getString(key);
	              System.err.println("key="+key+",v="+value);
	              Integer mId = map.get(key);
	              LinksMsg.dao.saveLikesMsg(access_token,mId,value,unionId,jdChannelId,key,getAdminId(),false);
		       }
		}
		renderTemplateJson("0", "成功转链");
	}


	/**
	 * 转链接记录
	 */
	@Before(JdLoginInterceptor.class)
	public void linksList(){
		Integer adminId = getAdminId();
		Long jduin = getJDUnionId();
		Integer pageNo = getParaToInt(0,1);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(" where 1 = ? ");
		List<Object> list = new ArrayList<Object>();
		list.add(1);
		buffer.append(" and m.unionId = ? ");
		list.add(jduin);
		buffer.append(" and m.adminId = ? ");
		list.add(adminId);
//		buffer.append(" and c.adminId = ? ");
//		list.add(adminId);
		String jdChannelId = getPara("jdChannelId");
		if(StringUtils.isNotBlank(jdChannelId)){
			buffer.append(" and m.jdChannelId = ? ");
			list.add(Integer.parseInt(jdChannelId));
		}
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate)){
			buffer.append(" and m.createDate between ? and ? ");
			list.add(startRegDate);
			list.add(endRegDate);
		}
		String uString = " m.linksMsgId,m.isSelf,m.statusType,m.goodsName,m.listenerMsgId,m.unionId,m.skuId,m.contents,m.createDate,m.GoodsPict ,c.spaceName ";
		Page<LinksMsg> page = LinksMsg.dao.paginate(pageNo,50, "select  "+uString,
				" from rb_linksmsg m "+
				" left join rb_jdchannel c on c.jdChannelId = m.jdChannelId "+buffer.toString()+
				" order by m.linksMsgId desc ",list.toArray());
		setAttr("page", page);
		List<WxJdChannel> channel = WxJdChannel.dao.find("select channelId,jdChannelId,spaceName from rb_jdchannel where unionId = ? ",jduin);
		setAttr("channel",channel);
	}
	/**
	 * 删除转链接记录
	 */
	public void deleteLinksMsg(){
		Integer linksId = getParaToInt("linksId");
		LinksMsg linksMsg = LinksMsg.dao.findById(linksId);
		if(linksMsg != null){
			String logInfo = "转链后的信息被删除：【主键："+linksId+",京东联盟ID："+linksMsg.getLong("unionId")
					+",skuId:"+linksMsg.getLong("skuId")
					+",商品名称："+linksMsg.getStr("goodsName")+"】";
			Admin admin = getAdmin();
			UserLogs.dao.saveLogsMsg(logInfo, LogType.LOG_UPDATE_MSG, admin.getInt("adminId"), admin.getStr("account"), CommonUtilsKit.getIpAddr(getRequest()));
			Db.update("delete from rb_linksmsg where linksMsgId = ? ",linksId);
			renderTemplateJson("0", "删除成功");
		}else {
			renderTemplateJson("-1", "删除失败");
		}
	}
		/**
		 * {"jingdong_service_promotion_wxsq_getCodeBySubUnionId_responce":{"code":"0","getcodebysubunionid_result":"{\"resultCode\":\"0\",\"resultMessage\":\"获取代码成功\",
		 * \"urlList\":{\"10084176121\":\"https://union-click.jd.com/jdc?d=gWX6oE\",\"11412208129\":\"https://union-click.jd.com/jdc?d=3Z7rVy\"}}"}}
		 */
		
	public void linksMsg(){
		Integer linksMsgId = getParaToInt(0);
		Integer adminId = getAdminId();
		setAttr("msg", LinksMsg.dao.findFirst("select m.*,c.spaceName from rb_linksmsg m left join rb_jdchannel c on c.jdChannelId = m.jdChannelId where m.linksMsgId = ? and m.adminId = ? ",linksMsgId,adminId));
		
	}
	
	
	public void findGroupRecord(){
		Integer pageNo = getParaToInt(0,1);
		StringBuffer buffer = new StringBuffer();
		List<Object>list = new ArrayList<>();
		buffer.append(" where 1 = ? ");
		list.add(1);
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate)){
			buffer.append(" and s.createDate between ? and ? ");
			list.add(startRegDate);
			list.add(endRegDate);
		}
		String url = " s.sendId,s.createDate ,g.channelId,g.NickName,lm.unionId,lm.goodsName,lm.GoodsPict,lm.contents,lm.statusType,lm.isSelf ";
		Page<SendGroupRecord> page = SendGroupRecord.dao.paginate(pageNo, 20,
				"select  "+url, 
				" from rb_sendgrouprecord s "
				+ " left join  rb_linksmsg lm on lm.linksMsgId = s.linksMsgId "
				+ " left join rb_wxusergroup g on g.groupId = s.groupId "+buffer.toString()
				+ " order by s.sendId desc ",list.toArray());
		for (SendGroupRecord send : page.getList()) {
			Integer channelId = send.getInt("channelId");
			if(channelId != null && channelId > 0){
				WxJdChannel channel = WxJdChannel.dao.findById(channelId);
				send.put("spaceName", channel.getStr("spaceName"));
			}else {
				send.put("spaceName", null);
			}
			
		}
		setAttr("page",page);
		
	} 
//	/**
//	 * 采集群列表设置有效
//	 */
//	@Before(Tx.class)
//	public void isEffect(){
//		Integer id = getParaToInt("id");	
//		WxUserGather wxUserGather = WxUserGather.dao.findById(id);
//		if(wxUserGather==null){
//			renderTemplateJson("-1","没有找到对应群");
//			return;
//		}
//		wxUserGather.set("isEffect", !wxUserGather.getBoolean("isEffect"));
//		wxUserGather.update();
//	}
	
}
