package com.raincc.robot.jd.jd;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.raincc.robot.jd.core.exceptions.LoginException;
import com.raincc.robot.jd.core.jdutils.HtmlUtils;
import com.raincc.robot.jd.core.jdutils.HttpUtils;
import com.raincc.robot.jd.core.jdutils.StringUtilsJD;

import static com.github.kevinsawicki.http.HttpRequest.METHOD_GET;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_POST;

public class Login {
	
	private static final String LoginPage="https://passport.jd.com/new/login.aspx";
	
	private static final String LoginUrl="https://passport.jd.com/uc/loginService";
	
	private static final String LoginSuccess="({\"success\":\"//www.jd.com\"})";
	
	private final static String SESSIONID="thor";//京东登录后的会话ID
	
	private static Logger logger=LoggerFactory.getLogger(Login.class);
	
	/**
	 * 登录账户和密码，返回登录成功后的cookie，如果登录失败则抛出登录异常
	 */
	public List<String> login(String loginname,String loginpwd){
		HttpRequest getLoginPageReq=HttpUtils.request(LoginPage, METHOD_GET);//请求登录页面
		String body=getLoginPageReq.body();
		Map<String, List<String>> responseHeaders=getLoginPageReq.headers();
		List<String> cookies=responseHeaders.get("Set-Cookie");//获取JSEESIONID
		logger.debug("请求登录页面响应结果为{}",body);
		Document doc=Jsoup.parse(body);//解析Html页面
		Elements form=doc.select("form#formlogin");//获取登录表单并且填写账号密码
		form.select("#loginname").val(loginname);
		form.select("#nloginpwd").val(loginpwd);
		form.select("#loginpwd").val(loginpwd);
		form.select("#authcode").val("vasd");
		Map<String,String> formData=HtmlUtils.formData(form);//构造登录表单数据
		formData.put("eid", "DE2C01C22FBCF776DCE2EAD33B9950BE720FFDD22945D78399F84BD18EC4CFECC030F455A2EB7267716043858060147F");
		formData.put("fp","57c2bd9af0067836ac64a23e2add8304");//js构造的表单参数
		String uuid=form.select("#uuid").val();
		String url=LoginUrl+"?uuid="+uuid+"&r="+Math.random()+"&version=2015";//登录请求地址
//		try{
//			Thread.sleep(2000);
//		}catch(InterruptedException e){
//			logger.error(e.getMessage());
//		}
		HttpRequest postLoginUrlReq=HttpUtils.request(url, METHOD_POST);
		String cookie=HtmlUtils.toCookieValue(cookies);
		Map<String,String> headers=new HashMap<>();
		headers.put("Cookie", cookie);
		postLoginUrlReq.headers(headers).form(formData);
		String body2=postLoginUrlReq.body();
		logger.info("登录请求的响应结果为{}",StringUtilsJD.decodeUnicode(body2));
		if(!LoginSuccess.equals(body2)){
			throw new LoginException("登录失败，登录返回数据为："+body2);
		}
		Map<String, List<String>> responseHeaders2=postLoginUrlReq.headers();
		List<String> cookies2=responseHeaders2.get("Set-Cookie");
		logger.info("登录响应的cookie为{}",cookies2);
		return cookies2;
	}
	
	
	
	public static void main(String[] args) {
		Login login=new Login();
		List<String> cookies=login.login("13810259504", "tianke111111@");
		OrderTest order=new OrderTest();
//		Order order = new Order();
		String cookie=HtmlUtils.toCookieValue(cookies);
		int year=2017;//订单时间
		System.out.println(order.getUserOrder(cookie,year));
	}
}
