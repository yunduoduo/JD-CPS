//package com.raincc.robot.me.test;
//
//
//import org.apache.log4j.Logger;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.blade.kit.DateKit;
//import com.blade.kit.StringKit;
//import com.blade.kit.http.HttpRequest;
//import com.jfinal.ext.route.ControllerBind;
//import com.raincc.robot.entity.TmpFlockList;
//import com.raincc.robot.me.biezhi.wechat.Constant;
//import com.raincc.robot.me.biezhi.wechat.WechatRobot;
//import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;
//import com.raincc.robot.me.biezhi.wechat.service.WechatService;
//import com.raincc.robot.me.biezhi.wechat.service.WechatServiceImpl;
//import com.raincc.robot.me.cncoder.record.RecordCon;
//import com.raincc.web.BaseController;
//
//@ControllerBind(controllerKey="/rob",viewPath="robot/login")
//public class RobtController extends BaseController {
//	protected static final Logger LOGGER = Logger.getLogger(RobtController.class);
//	String url = null;
//	public void index(){
//		WechatRobot wechatRobot = new WechatRobot();
//		getRequest().getSession().setAttribute("wae", wechatRobot);
//		url = wechatRobot.showQrCode();
//		setAttr("url", url);
//	}
//	public void login(){
//		try {
//			String acode = null;
//			WechatRobot wechatRobot = (WechatRobot)getRequest().getSession().getAttribute("wae");
//			if(wechatRobot == null){
//				LOGGER.info("用户没有获得session");
//				renderNull();
//				return ;
//			}
//
//			System.err.println("***wechatRobot="+wechatRobot.toString());
//			while(!Constant.HTTP_OK.equals(acode = wechatRobot.waitForLogin())){
//				if("-1".equals(acode) || "-2".equals(acode)){
//					LOGGER.info("二维码扫描或者登录失败");
//					renderNull();
//					return;
//				}else if("408".equals(acode)){
//					LOGGER.info("登录超时...");
//					renderNull();
//					return;
//				}
//				Thread.sleep(3000);
//			}
//			JSONObject jo = null;
//			if(Constant.HTTP_OK.equals(acode)){
//				jo = wechatRobot.userList();
//			}
////			renderText(wechatContact.toString());
//			JSONObject json = new JSONObject();
//			json.put("code", "0");
//			json.put("data", jo);
//			renderJson(json);
//			return;
//			//默认30分钟启动定时删除缓存
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	public void listener(){
//		WechatRobot wechatRobot = (WechatRobot)getRequest().getSession().getAttribute("wae");
//		if(wechatRobot == null){
//			LOGGER.info("listener——用户没有获得session");
//			renderNull();
//			return ;
//		}
////		删除session
//		getRequest().getSession().removeAttribute("wae");
//		WechatMeta wechatMeta = wechatRobot.listen();
//		getRequest().getSession().setAttribute("wechatMeta", wechatMeta);
//	}
//	
//	
//	public void flockList(){
//		TmpFlockList tmpFlockList = TmpFlockList.dao.findFirst("select * from tmp_flocklist ");
//		if(tmpFlockList == null){
//			renderNull();
//			return;
//		}
////		String object  = getCookie(Constant.CK_WECHATMETA_FLOCK);
//		String object = tmpFlockList.getStr("flockList");
////		object = Constant.unicodeToString(object);
//		JSONArray array = JSONArray.parseArray(object);
//		System.err.println(array.toString());
//		StringBuffer buffer = new StringBuffer();
//		for (Object object2 : array) {
//			JSONObject jsonObject = (JSONObject) object2;
//			LOGGER.info("用户群信息："+jsonObject.toString());
//			LOGGER.info("群用户UserName="+jsonObject.getString("UserName"));
//			String NickName = jsonObject.getString("NickName");
//			LOGGER.info("群NickName="+NickName);
//			buffer.append("群用户UserName="+jsonObject.getString("UserName")+",群NickName="+NickName+"\n");
//		}
//		renderText(buffer.toString());
//		return;
//	}
//	
//	public void stopListen(){
//		Constant.boStop = false;
//		renderNull();
//		return;
//	}
//
//	/**
//	 * 处理要发送的消息
//	 * @param wechatService
//	 * @param wechatMeta
//	 */
//	public void sendMsg(){
////		对象转json
//		WechatMeta wechatMeta = (WechatMeta)getRequest().getSession().getAttribute("wechatMeta");
//		
//		JSONObject jObject = Constant.objectTojson(wechatMeta);
//		//json转WechatMeta对象
////		WechatMeta wechat = Constant.JsonToObject(jObject);
//		TmpFlockList tmpFlockList = TmpFlockList.dao.findFirst("select * from tmp_flocklist ");
//		String toUserName = tmpFlockList.getStr("toUserName");
//		WechatService wechatService = new WechatServiceImpl();
//		com.blade.kit.json.JSONObject data = wechatService.webwxsync(wechatMeta);
//		if(data != null){
//			LOGGER.info(data.toString());
//		}
////		wechatService.handleMsg(wechatMeta, data);
//		toUserName = "@@f85a043014f59d9c20266987f5feb554897dc318e87e0f8da64a8c7943fe05f2";
//		webwxsendmsg(jObject, "cccc", toUserName);
//		renderNull();
//	}
//	/**
//	 * 发送消息
//	 */
//	private void webwxsendmsg(JSONObject meta, String content, String to) {
//		
//		String url = meta.getString("base_uri") + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + meta.getString("pass_ticket");
//		JSONObject body = new JSONObject();
//		//写入当前回复对象UserName
//		RecordCon.cache.add(to);
//		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
//		JSONObject Msg = new JSONObject();
//		Msg.put("Type", 1);
//		Msg.put("Content", content);
//		String userName = JSONObject.parseObject(meta.getString("User")).getString("UserName");
////		Msg.put("FromUserName", meta.getUser().getString("UserName"));
//		Msg.put("FromUserName", userName);
//		Msg.put("ToUserName", to);
//		Msg.put("LocalID", clientMsgId);
//		Msg.put("ClientMsgId", clientMsgId);
//
//		body.put("BaseRequest", meta.get("baseRequest"));
//		body.put("Msg", Msg);
//
//		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
//				.header("Cookie", meta.getString("cookie")).send(body.toString());
//
//		//LOGGER.info("发送消息...");
//		//LOGGER.debug("" + request);
//		request.body();
//		request.disconnect();
////		String url = meta.getBase_uri() + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + meta.getPass_ticket();
////		JSONObject body = new JSONObject();
////		//写入当前回复对象UserName
////		RecordCon.cache.add(to);
////		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
////		JSONObject Msg = new JSONObject();
////		Msg.put("Type", 1);
////		Msg.put("Content", content);
////		Msg.put("FromUserName", meta.getUser().getString("UserName"));
////		Msg.put("ToUserName", to);
////		Msg.put("LocalID", clientMsgId);
////		Msg.put("ClientMsgId", clientMsgId);
////
////		body.put("BaseRequest", meta.getBaseRequest());
////		body.put("Msg", Msg);
////
////		HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
////				.header("Cookie", meta.getCookie()).send(body.toString());
////
////		//LOGGER.info("发送消息...");
////		//LOGGER.debug("" + request);
////		request.body();
////		request.disconnect();
//	}
//	public static void main(String[] args) {
//		WechatRobot wechatRobot = new WechatRobot();
//		System.err.println("********url="+wechatRobot.showQrCode());
//		try {
//			String acode = null;
//			while(!Constant.HTTP_OK.equals(acode = wechatRobot.waitForLogin())){
//				if("-1".equals(acode) || "-2".equals(acode)){
//					LOGGER.info("二维码扫描或者登录失败");
//					return;
//				}else if("408".equals(acode)){
//					LOGGER.info("登录超时...");
//					return;
//				}
//				Thread.sleep(3000);
//			}
//			if(Constant.HTTP_OK.equals(acode)){
////				wechatRobot.start();
//			}
//			//默认30分钟启动定时删除缓存
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
