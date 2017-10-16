package com.raincc.robot.me.biezhi.wechat;

//import java.io.File;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.swing.UIManager;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


//import org.apache.commons.lang.StringUtils;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.kit.http.HttpRequest;
//import com.blade.kit.json.JSONArray;
//import com.blade.kit.json.JSONObject;
//import com.blade.kit.json.JSONValue;
//import com.jfinal.aop.Before;
//import com.jfinal.core.Controller;
//import com.jfinal.plugin.activerecord.Db;
//import com.jfinal.plugin.activerecord.tx.Tx;
//import com.raincc.robot.entity.TmpFlockList;
import com.raincc.robot.entity.WxUser;
import com.raincc.robot.entity.WxUserGroup;
import com.raincc.robot.me.biezhi.wechat.exception.WechatException;
import com.raincc.robot.me.biezhi.wechat.listener.WechatListener;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;
import com.raincc.robot.me.biezhi.wechat.service.WechatService;
import com.raincc.robot.me.biezhi.wechat.service.WechatServiceImpl;
//import com.raincc.robot.me.biezhi.wechat.ui.QRCodeFrame;
import com.raincc.robot.me.biezhi.wechat.util.CookieUtil;
import com.raincc.robot.me.biezhi.wechat.util.Matchers;
import com.raincc.robot.me.cncoder.record.RecordCon;

public class WechatRobot {

//	private static final Logger LOGGER = LoggerFactory.getLogger(WechatRobot.class);
	protected static final Logger LOGGER = Logger.getLogger(WechatRobot.class);

	private int tip = 0;
	private WechatListener wechatListener = new WechatListener();
	private WechatService wechatService = new WechatServiceImpl();
	private WechatMeta wechatMeta = new WechatMeta();
	
	
//	private QRCodeFrame qrCodeFrame;
	
	public WechatRobot() {
		System.setProperty("https.protocols", "TLSv1");
		System.setProperty("jsse.enableSNIExtension", "false");
	}
	
	/**
	 * 显示二维码
	 * 
	 * @return
	 */
	public String showQrCode() throws WechatException {
		String uuid = wechatService.getUUID();
		wechatMeta.setUuid(uuid);

//		LOGGER.info("获取到uuid为 [{}]", uuid);
		LOGGER.info("获取到uuid为 [{"+uuid+"}]");
		String url = Constant.QRCODE_URL + uuid;
		System.err.println("&&&&&&&url = "+url);
		return url;
//		final File output = new File("temp.jpg");
//		HttpRequest.post(url, true, "t", "webwx", "_", DateKit.getCurrentUnixTime()).receive(output);
		
		
//		if (null != output && output.exists() && output.isFile()) {
//			EventQueue.invokeLater(new Runnable() {
//				public void run() {
//					try {
//						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//						System.err.println("++++++++++output.getPath():"+output.getPath());
//						qrCodeFrame = new QRCodeFrame(output.getPath());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
//		}
	}

	/**
	 * 等待登录
	 */
	public String waitForLogin() throws WechatException {
		this.tip = 1;
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true, "tip", this.tip, "uuid", wechatMeta.getUuid(), "_",
				DateKit.getCurrentUnixTime());

		LOGGER.info("等待登录...");
		LOGGER.info("" + request.toString());

		String res = request.body();
		request.disconnect();
		System.err.println("res = "+res);
		if (null == res) {
//			throw new WechatException("扫描二维码验证失败");
			return "-1";
		}
		String code = Matchers.match("window.code=(\\d+);", res);
		if (null == code) {
//			throw new WechatException("扫描二维码验证失败");
			return "-2";
		} else {
			if (code.equals("201")) {
				LOGGER.info("成功扫描,请在手机上点击确认以登录");
				tip = 0;
			} else if (code.equals("200")) {
				LOGGER.info("正在登录...");
				String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);
				String redirect_uri = pm + "&fun=new";
				wechatMeta.setRedirect_uri(redirect_uri);
				
				String base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));
				wechatMeta.setBase_uri(base_uri);
				
//				LOGGER.info("redirect_uri={}", redirect_uri);
//				LOGGER.info("base_uri={}", base_uri);
				LOGGER.info("redirect_uri={"+redirect_uri+"}");
				LOGGER.info("base_uri={"+base_uri+"}");
			} else if (code.equals("408") || code.equals("400")) {
//				throw new WechatException("登录超时");
				LOGGER.info("登录超时...");
				return "408";
			} else {
//				LOGGER.info("扫描code={}", code);
				LOGGER.info("扫描code={"+code+"}");
			}
		}
		return code;
	}
	/**
	 * 关闭图片弹窗
	 */
//	public void closeQrWindow() {
//		qrCodeFrame.dispose();
//	}

	/**
	 * 登录
	 */
	public void login() throws WechatException {
		HttpRequest request = HttpRequest.get(wechatMeta.getRedirect_uri());
		
		LOGGER.debug("" + request);
		String res = request.body();
//		获取cookie
		wechatMeta.setCookie(CookieUtil.getCookie(request));
		request.disconnect();

		if (StringKit.isBlank(res)) {
			throw new WechatException("登录失败");
		}
		wechatMeta.setSkey(Matchers.match("<skey>(\\S+)</skey>", res));
		wechatMeta.setWxsid(Matchers.match("<wxsid>(\\S+)</wxsid>", res));
		wechatMeta.setWxuin(Matchers.match("<wxuin>(\\S+)</wxuin>", res));
		wechatMeta.setPass_ticket(Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res));

		JSONObject baseRequest = new JSONObject();
		baseRequest.put("Uin", wechatMeta.getWxuin());
		baseRequest.put("Sid", wechatMeta.getWxsid());
		baseRequest.put("Skey", wechatMeta.getSkey());
		baseRequest.put("DeviceID", wechatMeta.getDeviceId());
		wechatMeta.setBaseRequest(baseRequest);
		
//		LOGGER.info("skey [{}]", wechatMeta.getSkey());
//		LOGGER.info("wxsid [{}]", wechatMeta.getWxsid());
//		LOGGER.info("wxuin [{}]", wechatMeta.getWxuin());
//		LOGGER.info("pass_ticket [{}]", wechatMeta.getPass_ticket());
		LOGGER.info("skey [{"+wechatMeta.getSkey()+"}]");
		LOGGER.info("wxsid [{"+wechatMeta.getWxsid()+"}]");
		LOGGER.info("wxuin [{"+wechatMeta.getWxuin()+"}]");
		LOGGER.info("pass_ticket [{"+ wechatMeta.getPass_ticket()+"}]");
//		File output = new File("temp.jpg");
//		if(output.exists()){
//			output.delete();
//		}
		
		
	}
	public JSONObject userList(Integer adminId) {
		this.login();
		LOGGER.info("微信登录成功");
		
		LOGGER.info("微信初始化...");
		wechatService.wxInit(wechatMeta);
		LOGGER.info("微信初始化成功");
		
		LOGGER.info("开启状态通知...");
		wechatService.openStatusNotify(wechatMeta);
		LOGGER.info("开启状态通知成功");
		
//		LOGGER.info("获取联系人...");
//		Constant.CONTACT = wechatService.getContact(wechatMeta);
//		LOGGER.info("获取联系人成功");
////		LOGGER.info("共有 {} 位联系人", Constant.CONTACT.getContactList().size());
//		LOGGER.info("共有 {"+Constant.CONTACT.getContactList().size()+"} 位联系人");
//		System.out.println("WechatRobot.start(),联系人这Constant.CONTACT="+Constant.CONTACT.toString());
//		System.out.println("WechatRobot.start(),联系人这Constant.CONTACT.getContactList()="+Constant.CONTACT.getContactList().toString()+",个数："+Constant.CONTACT.getContactList().size());
		
		LOGGER.info("当前用户登录用户为：UserName="+wechatMeta.getUser().getString("UserName")+",用户NickName="+wechatMeta.getUser().getString("NickName")+",署名为Signature="+wechatMeta.getUser().getString("Signature"));
//		c.setCookie(Constant.CK_WECHATMETA,wechatMeta.toString(), 60 * 60 * 24 * 1);
		Integer Uin = wechatMeta.getUser().getIntValue("Uin");
		if(WxUser.dao.findFirst("select Uin from rb_wxuser where Uin = ? ",Uin) == null){
			WxUser.dao.saveUser(wechatMeta,adminId);
		}else {
			//更新用户信息
			WxUser.dao.upUser(wechatMeta);
		}
		LOGGER.info("获取联系群...");
		Constant.CONTACT = wechatService.getFlockContact(wechatMeta);
//		String aString = Constant.stringToUnicode(Constant.CONTACT.getContactList().toString());
//		c.setCookie(Constant.CK_WECHATMETA_FLOCK, aString, 60 * 60 * 24 * 1);

//		临时存储登录用户
//		TmpFlockList.dao.flockSave(wechatMeta);
		
		LOGGER.info("获取联系群成功");
		LOGGER.info("共有 {"+Constant.CONTACT.getContactList().size()+"} 群");
		System.out.println("WechatRobot.start(),联系人这Constant.CONTACT="+Constant.CONTACT.toString());
		System.out.println("WechatRobot.start(),联系人这Constant.CONTACT.getContactList()="+Constant.CONTACT.getContactList().toString()+",个数："+Constant.CONTACT.getContactList().size());
		JSONArray jsonArray  = Constant.CONTACT.getContactList();
//		//写入群
//		RecordCon recordCon=new RecordCon();
//		recordCon.writeCon(wechatMeta);
		// 监听消息
//		wechatListener.start(wechatService, wechatMeta,c);
		JSONObject jsonObject = WxUserGroup.dao.groupList(jsonArray,wechatMeta);
		jsonObject.put("uin",wechatMeta.getUser().getInteger("Uin"));
		return jsonObject;
//		if(StringUtils.isNotBlank(UserName)){ 
//			test(wechatMeta, "测试哈哈", UserName);
//		}
	}
	
	//开始监听消息
	public WechatMeta listen(Integer adminId){
		wechatListener.start(wechatService, wechatMeta,adminId);
		return wechatMeta;
	}
	@SuppressWarnings("unused")
	private void test(WechatMeta meta, String content, String to){
		String url = meta.getBase_uri() + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + meta.getPass_ticket();
		JSONObject body = new JSONObject();
		//写入当前回复对象UserName
		RecordCon.cache.add(to);
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 1);
		Msg.put("Content", content);
		Msg.put("FromUserName", meta.getUser().getString("UserName"));
		Msg.put("ToUserName", to);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);

		body.put("BaseRequest", meta.getBaseRequest());
		body.put("Msg", Msg);

		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
				.header("Cookie", meta.getCookie()).send(body.toString());

		//LOGGER.info("发送消息...");
		//LOGGER.debug("" + request);
		request.body();
		request.disconnect();
	}
	
}