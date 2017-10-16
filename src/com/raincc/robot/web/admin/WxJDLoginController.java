package com.raincc.robot.web.admin;

import static com.github.kevinsawicki.http.HttpRequest.METHOD_GET;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_POST;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.common.RcConstants;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.model.Admin;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.WxJdChannel;
import com.raincc.robot.entity.WxJdUser;
import com.raincc.robot.jd.core.jdutils.HtmlUtils;
import com.raincc.robot.jd.core.jdutils.HttpUtils;
import com.raincc.robot.jd.core.jdutils.StringUtilsJD;
import com.raincc.robot.jd.core.jdutils.TUtils;
@ControllerBind(controllerKey="/robotAdmin/rob/jd/login",viewPath="admin/robot/jd/login")
@Before(AdminInterceptor.class)
public class WxJDLoginController extends AdminBaseController {

	public void testLogin(){
		Map<String,String> maps = TUtils.getCookieToLogin(TConstants.T_LOGINNAME, TConstants.T_PASSWORD);
		String code = maps.get("code");
		if("0".equals(code)){
			_log.info("登录更新成功");
		}else {
			_log.info("登录更新失败，原因是："+maps.get("info"));
		}
	}
	/**
	 * 京东登录获取cookie
	 * @throws Exception 
	 */
	public void index(){
		setAttr("loginname",getCookie(TConstants.JD_LOGINNAME));
		setAttr("password",getCookie(TConstants.JD_LOGINPWD));
		HttpRequest getLoginPageReq=HttpUtils.request(TConstants.JD_LoginPage, METHOD_GET);//请求登录页面
		getLoginPageReq.header("Referer", "https://passport.jd.com/new/login.aspx");
		String body=getLoginPageReq.body();
		Map<String, List<String>> responseHeaders=getLoginPageReq.headers();
		List<String> cookies=responseHeaders.get("Set-Cookie");//获取JSEESIONID
		String cookie=HtmlUtils.toCookieValue(cookies);
		setAttr("cookie", cookie);
		_log.debug("请求登录页面响应结果为{}"+body);
		Document doc=Jsoup.parse(body);
		String  eString = doc.getElementById("JD_Verification1").toString();
		setAttr("imgString",eString);
		String src = doc.getElementById("JD_Verification1").attr("src2");
		setAttr("src", src);
		Elements form=doc.select("#formlogin");
		setSessionAttr("formlogin",form.toString());
		setSessionAttr("jd_cookie",cookie);
	}
	
	/**
	 * 更新验证码
	 */
	public void upAuthcode(){
		String imgsrc = getPara("imgsrc");
		String code_Img = getPara("code_Img");
		String imgUrl = TUtils.getCodeImg(imgsrc,PathKit.getWebRootPath());
		if(StringUtils.isNotBlank(imgUrl) && StringUtils.isNotBlank(code_Img)){
			File file = new File(PathKit.getWebRootPath() + code_Img);
			if (file.isFile() && file.exists()) {
				file.delete();
				_log.info("删除验证码图片释放内存");
			}
		}
		renderTemplateJson("0", imgUrl);
	}
	/**
	 * 判断验证码
	 */
	public void showAuthCode(){
		String loginName = getPara("loginName");
		String cookie = getPara("cookie");
		String imgsrc = getPara("imgsrc");
		Map<String, String>formData = new HashMap<String,String>();
		formData.put("loginName",loginName);
		String url=TConstants.JD_showAuthCode+"?r="+Math.random()+"&version=2015";
		HttpRequest postLoginUrlReq=HttpUtils.request(url, METHOD_POST);
		Map<String,String> headers=new HashMap<>();
		headers.put("Accept","text/plain, */*; q=0.01");
		headers.put("Content-Type", TConstants.ContentType_FORMDATA);
		headers.put("Cookie",cookie);
		headers.put("Referer", "https://passport.jd.com/new/login.aspx");
		headers.put("X-Requested-With","XMLHttpRequest");
		postLoginUrlReq.headers(headers).form(formData);
		String bodyss=postLoginUrlReq.body();
		_log.info(bodyss);
		String imgUrl = "";
		if(bodyss.contains("true")){
			imgUrl = TUtils.getCodeImg(imgsrc,PathKit.getWebRootPath());
		}
		JSONObject jo = new JSONObject();
		jo.put("code", "0");
		jo.put("codeImg", imgUrl);
		jo.put("info", bodyss);
		renderJson(jo);
	}

	
	/**
	 * 帐号密码登录，并异步更新渠道和商品
	 */
	public void jdLogin() {
		String loginname = getPara("loginname");
		String password = getPara("password");
		String code_Img = getPara("code_Img");
		if(StringUtils.isBlank(loginname) || StringUtils.isBlank(password)){
			renderTemplateJson("-1", "帐号或者密码不能为空");
			return;
		}
		String authcode = getPara("authcode");
		String formlogin = getSessionAttr("formlogin");
		String cookie = getSessionAttr("jd_cookie");
		if(StringUtils.isBlank(formlogin) || StringUtils.isBlank(cookie)){
			renderTemplateJson("","系统有误，请您重新刷新登录");
			return;
		}
		removeSessionAttr("formlogin");removeSessionAttr("jd_cookie");
		Document doc=Jsoup.parse(formlogin);//解析Html页面
		Elements form=doc.select("#formlogin");//获取登录表单并且填写账号密码
		form.select("#loginname").val(loginname);
		form.select("#nloginpwd").val(password);
		form.select("#authcode").val(authcode);
		
		Map<String,String> formData=HtmlUtils.formData(form);//构造登录表单数据
//		formData.put("eid", TConstants.T_JDLOGIN_EID);
//		formData.put("fp",TConstants.T_LDLOGIN_FP);//js构造的表单参数
		String eid = getPara("eid");
		String fp = getPara("fp");
		formData.put("eid",eid);
		formData.put("fp",fp);//js构造的表单参数
		String seqSid = getPara("seqSid");
		formData.put("chkRememberMe","");
		formData.put("seqSid",seqSid);
		String uuid=form.select("#uuid").val();
		String url=TConstants.JD_LoginUrl+"?uuid="+uuid+"&r="+Math.random()+"&version=2015";//登录请求地址
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){
			_log.error(e.getMessage());
		}
		HttpRequest postLoginUrlReq=HttpUtils.request(url, METHOD_POST);
		Map<String,String> headers=new HashMap<>();
		headers.put("Cookie", cookie);
		headers.put("Referer", "https://passport.jd.com/new/login.aspx");
		postLoginUrlReq.headers(headers).form(formData);
		String body2=postLoginUrlReq.body();
		_log.info("登录请求的响应结果为{}"+StringUtilsJD.decodeUnicode(body2));
		
		if(body2.contains("success") && body2.contains("//www.jd.com")){
			Map<String, List<String>> responseHeaders2=postLoginUrlReq.headers();
			List<String> cookies2=responseHeaders2.get("Set-Cookie");
			_log.info("登录响应的cookie为{}"+cookies2);
			//记录登录用户
			setCookie(TConstants.JD_LOGINNAME,loginname,1 * 24 * 60 * 60);
			setCookie(TConstants.JD_LOGINPWD,password,1 * 24 * 60 * 60);
			Admin admin = (Admin)getSession().getAttribute(RcConstants.session_login_admin);
			Integer adId = admin.getInt("adminId");
			String cookieString = HtmlUtils.toCookieValue(cookies2);
			Boolean bo = getIndividual();//判断是否是个人用户
			Integer id = WxJdUser.dao.saveUser(loginname,password,cookieString,adId,bo);
			getSession().setAttribute(RcConstants.session_login_admin,Admin.dao.findById(adId));
//			设置缓存jduserid
			getSession().setAttribute(TConstants.JD_USERID,id);
			if(StringUtils.isNotBlank(code_Img)){
				//删除验证码图片释放内存
				File file = new File(PathKit.getWebRootPath() + code_Img);
				if (file.isFile() && file.exists()) {
					file.delete();
					_log.info("删除验证码图片释放内存");
				}
			}
			Integer adminId = getAdminId();
//			更新渠道和渠道中的商品
			upJdChannel(id,cookieString,adminId);
			renderTemplateJson("0","登录成功");
		}else {
			renderTemplateJson("-1","验证码不正确或验证码已过期");
			return;
		}
		
	}
	

	/**更新推广位
	 * @param id
	 * @param cookie
	 * @param adminId
	 */
	private void upJdChannel(final Integer id, final String cookie,final Integer adminId) {
		 new Thread(new Runnable() {
				@Override
				public void run() {
					HttpRequest getOrderPageReq=HttpUtils.request(TConstants.JD_CHANNEL, METHOD_POST);//请求订单页面
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("Content-Type","application/x-www-form-urlencoded" );
					data.put("id",0);
					data.put("type", 4);
					data.put("status", 1);
					getOrderPageReq.header("Cookie", cookie).form(data);
					String body=getOrderPageReq.body();
					JSONObject jObject = JSONObject.parseObject(body);
					JSONArray jsonArray = jObject.getJSONArray("promotionSite");
					if(jsonArray.size() > 0){
						JSONObject j = jsonArray.getJSONObject(0);
						Db.update("update rb_wxjduser set unionId = ? where accountsId = ? ",j.getInteger("unionId"),id);
					}
					for (int i = 0; i < jsonArray.size(); i++) {
						JSONObject jo = jsonArray.getJSONObject(i);
						WxJdChannel.dao.saveChannel(jo,adminId);
					}
				}
			}).start();
	}


}
