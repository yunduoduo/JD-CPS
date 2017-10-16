package com.raincc.robot.me.biezhi.wechat.service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.FileKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.ListenerMsg;
import com.raincc.robot.entity.WxUserGroup;
import com.raincc.robot.me.biezhi.wechat.Constant;
import com.raincc.robot.me.biezhi.wechat.exception.WechatException;
import com.raincc.robot.me.biezhi.wechat.model.WechatContact;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;
import com.raincc.robot.me.biezhi.wechat.util.Matchers;

public class WechatServiceImpl implements WechatService {

	protected static final Logger LOGGER = Logger.getLogger(WechatService.class);
	
	// 茉莉机器人
	private static boolean imgBo = false;
	private static ListenerMsg listenerMsg = null;
	

	/**
	 * 处理消息
	 */
	@Override
	public void handleMsg(WechatMeta wechatMeta, JSONObject data,Integer adminId,Long Uin) {
		if (null == data) {
			return;
		}
		System.out.println("WechatServiceImpl.handleMsg(),最新消息："+data.toString());

		JSONArray AddMsgList = data.getJSONArray("AddMsgList");
		
		List<WxUserGroup> wxUserGroup = WxUserGroup.dao.find("select toUserName from rb_wxusergroup where isValid = 1 and isPutaway = true and Uin = ? ",Uin);
		String usernameString = wechatMeta.getUser().getString("UserName");
		for (int i = 0, len = AddMsgList.size(); i < len; i++) {
			LOGGER.info("你有新的消息，请注意查收");
			JSONObject msg = AddMsgList.getJSONObject(0);
			String FUserName = msg.getString("FromUserName");
			//String toUName [] = toUserName.split(",");
			for (WxUserGroup wx : wxUserGroup) {
				String toUName = wx.getStr("toUserName");
				if(toUName.equals(FUserName)){
					Integer msgType = msg.getIntValue("MsgType");
					String name = getUserRemarkName(msg.getString("FromUserName"));
					LOGGER.info("消息来自于："+name);
					String content = msg.getString("Content");
					LOGGER.info("消息的内容为："+content);
					if(content.contains(":<br/>")){
						content = content.substring(content.indexOf(":<br/>")+6);
					}
					if(StringUtils.isNotBlank(content.trim())){
						LOGGER.info("处理后的消息内容为："+content);
						String FromUserName = msg.getString("FromUserName");
						String GroupName = name;
						if(!content.startsWith("&lt;?xml")){
							ByteArrayOutputStream baoStream = new ByteArrayOutputStream(1024);
				            PrintStream cacheStream = new PrintStream(baoStream);
				            PrintStream oldStream = System.out;
				            System.setOut(cacheStream);
				            content = content.replace("<br/>", "\n");
				            System.out.println(content);
				            String message = baoStream.toString();
				            System.setOut(oldStream);
				            System.out.println(message);
							ListenerMsg listen = ListenerMsg.dao.saveMsg(wechatMeta,FromUserName,GroupName,message,adminId);
							listenerMsg = listen;
						}
						if (msgType == 51) {
							//LOGGER.info("成功截获微信初始化消息");
						} else if (msgType == 3) {
								if(listenerMsg != null){
									String imgDir = PathKit.getWebRootPath()+"/img";
									String msgId = msg.getString("MsgId");
									FileKit.createDir(imgDir, false);
									String imgUrl = wechatMeta.getBase_uri() + "/webwxgetmsgimg?MsgID=" + msgId + "&skey=" + wechatMeta.getSkey() + "&type=slave";
									HttpRequest.get(imgUrl).header("Cookie", wechatMeta.getCookie()).receive(new File(imgDir + "/" + msgId+".jpg"));
									System.out.println("WechatServiceImpl.handleMsg()下载图片地址为："+imgDir + "/" + msgId+".jpg");
									String imgs = TConstants.PJECT_URL+"/img" + "/" + msgId+".jpg";
									Integer listenerMsgId = listenerMsg.getInt("listenerMsgId");
									Db.update("update rb_listener_msg set imgUrl = ? where listenerMsgId = ? ",imgs,listenerMsgId);
									listenerMsg = null;
									//图片接收完毕，开始自动转链接
									ListenerMsg.dao.toLink(Uin,listenerMsgId,TConstants.BL_JD_UNIONID,adminId);
									
							}
//							发送消息
//							webwxsendmsg(wechatMeta, "无法查看图片", msg.getString("FromUserName"));
//							continue;
						
						} else if (msgType == 1) {
						
						} else if (msgType == 34) {
//							webwxsendmsg(wechatMeta, "语音也听不懂", msg.getString("FromUserName"));
						} else if (msgType == 42) {
							LOGGER.info(name + " 给你发送了一张名片:");
						}
					}
				}
			}
			
		}
	}
	/**
	 * 微信初始化
	 */
	@Override
	public void wxInit(WechatMeta wechatMeta) throws WechatException {
		String url = wechatMeta.getBase_uri() + "/webwxinit?r=" + DateKit.getCurrentUnixTime() + "&pass_ticket="
				+ wechatMeta.getPass_ticket() + "&skey=" + wechatMeta.getSkey();
		JSONObject body = new JSONObject();
		body.put("BaseRequest", wechatMeta.getBaseRequest());
		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
				.header("Cookie", wechatMeta.getCookie()).send(body.toString());
		LOGGER.debug("" + request);
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			throw new WechatException("微信初始化失败");
		}
		try {
			JSONObject jsonObject = JSONObject.parseObject(res);
			if (null != jsonObject) {
				JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
				if (null != BaseResponse) {
					Integer ret = BaseResponse.getIntValue("Ret");
					if (ret != null && ret == 0) {
						//{"Count":4,"List":[{"Key":1,"Val":662570900},{"Key":2,"Val":662570928},{"Key":3,"Val":662570892},{"Key":1000,"Val":1501030261}]}
						wechatMeta.setSyncKey(jsonObject.getJSONObject("SyncKey"));
						wechatMeta.setUser(jsonObject.getJSONObject("User"));
						StringBuffer synckey = new StringBuffer();
						JSONArray list = wechatMeta.getSyncKey().getJSONArray("List");
						for (int i = 0, len = list.size(); i < len; i++) {
							JSONObject item = list.getJSONObject(i);;
							synckey.append("|" + item.getIntValue("Key") + "_" + item.getIntValue("Val"));
						}
						wechatMeta.setSynckey(synckey.substring(1));
					}
				}
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * 打开状态提醒
	 */
	@Override
	public void openStatusNotify(WechatMeta wechatMeta) throws WechatException {
		String url = wechatMeta.getBase_uri() + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + wechatMeta.getPass_ticket();
		JSONObject body = new JSONObject();
		body.put("BaseRequest", wechatMeta.getBaseRequest());
		body.put("Code", 3);
		body.put("FromUserName", wechatMeta.getUser().getString("UserName"));
		body.put("ToUserName", wechatMeta.getUser().getString("UserName"));
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());
		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
				.header("Cookie", wechatMeta.getCookie()).send(body.toString());
		LOGGER.debug("" + request);
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			throw new WechatException("状态通知开启失败");
		}
		try {
			JSONObject jsonObject = JSONObject.parseObject(res);
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				Integer ret = BaseResponse.getIntValue("Ret");
				if (ret !=null && ret != 0) {
					throw new WechatException("状态通知开启失败，ret：" + ret);
				}
			}
		} catch (Exception e) {
			throw new WechatException(e);
		}
	}

	
	
	/**
	 * 选择同步线路
	 */
	@Override
	public void choiceSyncLine(WechatMeta wechatMeta) throws WechatException {
		boolean enabled = false;
		for(String syncUrl : Constant.SYNC_HOST){
			int[] res = this.syncCheck(syncUrl, wechatMeta);
			if(res[0] == 0){
				String url = "https://" + syncUrl + "/cgi-bin/mmwebwx-bin";
				wechatMeta.setWebpush_url(url);
				LOGGER.info("选择线路：[{"+syncUrl+"}]");
				enabled = true;
				break;
			}
		}
		if(!enabled){
			throw new WechatException("同步线路不通畅");
		}
	}
	
	/**
	 * 检测心跳
	 */
	@Override
	public int[] syncCheck(WechatMeta wechatMeta) throws WechatException{
		return this.syncCheck(null, wechatMeta);
	}
	
	/**
	 * 检测心跳
	 */
	private int[] syncCheck(String url, WechatMeta meta) throws WechatException{
		if(null == url){
			url = meta.getWebpush_url() + "/synccheck";
		} else{
			url = "https://" + url + "/cgi-bin/mmwebwx-bin/synccheck";
		}
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", meta.getBaseRequest());
		HttpRequest request = HttpRequest
				.get(url, true, "r", DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5), "skey",
						meta.getSkey(), "uin", meta.getWxuin(), "sid", meta.getWxsid(), "deviceid",
						meta.getDeviceId(), "synckey", meta.getSynckey(), "_", System.currentTimeMillis())
				.header("Cookie", meta.getCookie());

		LOGGER.debug(request.toString());
		String res = "";
		try {
			res = request.body();
		} catch (Exception e) {
			throw new WechatException("心跳检测失败");
		}
		request.disconnect();

		int[] arr = new int[]{-1, -1};
		if (StringKit.isBlank(res)) {
			return arr;
		}
		String retcode = Matchers.match("retcode:\"(\\d+)\",", res);
		String selector = Matchers.match("selector:\"(\\d+)\"}", res);
		if (null != retcode && null != selector) {
			arr[0] = Integer.parseInt(retcode);
			arr[1] = Integer.parseInt(selector);
			return arr;
		}
		return arr;
	}

	
	
	
	/**获取来源人姓名
	 * @param id
	 * @return
	 */
	private String getUserRemarkName(String id) {
		String name = "这个人物名字未知";
		for (int i = 0, len = Constant.CONTACT.getMemberList().size(); i < len; i++) {
			
			JSONObject member = Constant.CONTACT.getMemberList().getJSONObject(i);
			if (member.getString("UserName").equals(id)) {
				if (StringKit.isNotBlank(member.getString("RemarkName"))) {
					name = member.getString("RemarkName");
				} else {
					name = member.getString("NickName");
				}
				return name;
			}
		}
		return name;
	}
	/**
	 *  获取最新消息
	 */
	@Override
	public JSONObject webwxsync(WechatMeta meta) throws WechatException{
		String url = meta.getBase_uri() + "/webwxsync?skey=" + meta.getSkey() + "&sid=" + meta.getWxsid();
		JSONObject body = new JSONObject();
		body.put("BaseRequest", meta.getBaseRequest());
		body.put("SyncKey", meta.getSyncKey());
		body.put("rr", DateKit.getCurrentUnixTime());
		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
				.header("Cookie", meta.getCookie()).send(body.toString());
		LOGGER.info(request.toString());
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			throw new WechatException("同步syncKey失败");
		}
		JSONObject jsonObject = JSONObject.parseObject(res);
		JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
		if (null != BaseResponse) {
			Integer ret = BaseResponse.getIntValue("Ret");
			if (ret != null && ret == 0) {
				meta.setSyncKey(jsonObject.getJSONObject("SyncKey"));
				StringBuffer synckey = new StringBuffer();
				JSONArray list = meta.getSyncKey().getJSONArray("List");
				for (int i = 0, len = list.size(); i < len; i++) {
					JSONObject item = list.getJSONObject(i);
					synckey.append("|" + item.getIntValue("Key") + "_" + item.getIntValue("Val"));
				}
				meta.setSynckey(synckey.substring(1));
				return jsonObject;
			}
		}
		return null;
	}

	/**
	 * 获取组 群
	 */
	@Override
	public WechatContact getFlockContact(WechatMeta wechatMeta) {
		String url = wechatMeta.getBase_uri() + "/webwxgetcontact?pass_ticket=" + wechatMeta.getPass_ticket() + "&skey="
				+ wechatMeta.getSkey() + "&r=" + DateKit.getCurrentUnixTime();

		JSONObject body = new JSONObject();
		body.put("BaseRequest", wechatMeta.getBaseRequest());

		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
				.header("Cookie", wechatMeta.getCookie()).send(body.toString());
		
		LOGGER.debug(request.toString());
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			throw new WechatException("获取联系人失败");
		}
		LOGGER.debug("res="+res);
		WechatContact wechatContact = new WechatContact();
		try {
			JSONObject jsonObject = JSONObject.parseObject(res);
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				Integer ret = BaseResponse.getIntValue("Ret");
				if (ret != null && ret == 0) {
					JSONArray memberList = jsonObject.getJSONArray("MemberList");
					JSONArray contactList = new JSONArray();
					
					if (null != memberList) {
						for (int i = 0, len = memberList.size(); i < len; i++) {
							JSONObject contact = memberList.getJSONObject(i);
							// 群聊
							if (contact.getString("UserName").indexOf("@@") != -1) {
								contactList.add(contact);
							}
						}
						wechatContact.setContactList(contactList);
						wechatContact.setMemberList(memberList);
//						this.getGroup(wechatMeta, wechatContact);
						System.out.println(wechatContact.toString());
						return wechatContact;
					}
				}
			}
		} catch (Exception e) {
			throw new WechatException(e);
		}
		return null;
	}
	/**
	 * 获取UUID
	 */
	@Override
	public String getUUID() throws WechatException {
		HttpRequest request = HttpRequest.get(Constant.JS_LOGIN_URL, true, "appid", "wx782c26e4c19acffb", "fun", "new",
				"lang", "zh_CN", "_", DateKit.getCurrentUnixTime());
		LOGGER.debug(request.toString());
		String res = request.body();
		request.disconnect();
//		res=window.QRLogin.code = 200; window.QRLogin.uuid = "QdJSLdnfow==";
		if (StringKit.isNotBlank(res)) {
			String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
			if (null != code) {
				if (code.equals("200")) {
					String uuid =  Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
					return uuid;
				} else {
					throw new WechatException("错误的状态码: " + code);
				}
			}
		}
		throw new WechatException("获取UUID失败");
	}
	/**
	 * 获取联系人
	 */
	@Override
	public WechatContact getContact(WechatMeta wechatMeta) {
		return null;
	}
	/**
	 * 发送消息
	 */
	@SuppressWarnings("unused")
	private void webwxsendmsg(WechatMeta meta, String content, String to) {
//		String url = meta.getBase_uri() + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + meta.getPass_ticket();
//		JSONObject body = new JSONObject();
//		//写入当前回复对象UserName
//		RecordCon.cache.add(to);
//		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
//		JSONObject Msg = new JSONObject();
//		Msg.put("Type", 1);
//		Msg.put("Content", content);
//		Msg.put("FromUserName", meta.getUser().getString("UserName"));
//		Msg.put("ToUserName", to);
//		Msg.put("LocalID", clientMsgId);
//		Msg.put("ClientMsgId", clientMsgId);
//
//		body.put("BaseRequest", meta.getBaseRequest());
//		body.put("Msg", Msg);
//
//		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
//				.header("Cookie", meta.getCookie()).send(body.toString());
//		request.body();
//		request.disconnect();
	}
	
}
