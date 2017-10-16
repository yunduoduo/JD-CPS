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
import com.google.inject.spi.Element;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.robot.jd.core.exceptions.LoginException;
import com.raincc.robot.jd.core.jdutils.HtmlUtils;
import com.raincc.robot.jd.core.jdutils.HttpUtils;
import com.raincc.robot.jd.core.jdutils.StringUtilsJD;
import com.raincc.robot.web.admin.AdminBaseController;
import com.raincc.web.BaseController;

import static com.github.kevinsawicki.http.HttpRequest.METHOD_GET;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_POST;


public class LoginTest extends AdminBaseController{

	private static final String LoginPage="https://passport.jd.com/new/login.aspx";
//	private static final String LoginPage="https://passport.jd.com/common/loginPage?from=media&ReturnUrl=https%3A%2F%2Fmedia.jd.com%2FloginJump";
//										  http://passport.jd.com/common/loginPage?from=media&amp;ReturnUrl=https%3A%2F%2Fmedia.jd.com%2FloginJump
//	private static final String LoginUrl="https://passport.jd.com/uc/loginService";
	private static final String LoginUrl="http://passport.jd.com/common/loginService";
//	private static final String LoginSuccess="({\"success\":\"http://www.jd.com\"})";
//	我的修改
	private static final String LoginSuccess="({\"success\":\"//www.jd.com\"})";
	private final static String SESSIONID="thor";//京东登录后的会话ID
	
	private static Logger logger=LoggerFactory.getLogger(LoginTest.class);
	public void index(){
		HttpRequest getLoginPageReq=HttpUtils.request(LoginPage, METHOD_GET);//请求登录页面
		String body=getLoginPageReq.body();
		Map<String, List<String>> responseHeaders=getLoginPageReq.headers();
		List<String> cookies=responseHeaders.get("Set-Cookie");//获取JSEESIONID
		logger.debug("请求登录页面响应结果为{}",body);
		Document doc=Jsoup.parse(body);
		String eString = doc.getElementById("o-authcode").html();
		String src = "";
		boolean bo = false;//是否需要验证码
		if(eString.contains("hide")){//不需要验证码
			
		}else {//需要验证码
			src = doc.getElementById("JD_Verification1").attr("src");
			System.err.println(src);
			bo = true;
		}
		setAttr("bo", bo);
		setAttr("src", src);
		setCookie("jdlog",doc.toString(),60 * 60 * 24 * 1 );
		String cookie=HtmlUtils.toCookieValue(cookies);
		setCookie("cookie",cookie,60 * 60 * 24 * 1 );
		
	}
	public void login(){
		Document doc = Jsoup.parse(getCookie("jdlog"));
		String cookie = getCookie("cookie");
		removeCookie("jdlog");		removeCookie("cookie");
		String a = getCookie("jdlog");
		String cookiess = getCookie("cookie");
		
	}
	
	/**
	 * 登录账户和密码，返回登录成功后的cookie，如果登录失败则抛出登录异常
	 */
	public static List<String> login(String loginname,String loginpwd){
		HttpRequest getLoginPageReq=HttpUtils.request(LoginPage, METHOD_GET);//请求登录页面
		String body=getLoginPageReq.body();
		Map<String, List<String>> responseHeaders=getLoginPageReq.headers();
		List<String> cookies=responseHeaders.get("Set-Cookie");//获取JSEESIONID
		logger.debug("请求登录页面响应结果为{}",body);
		Document doc=Jsoup.parse(body);//解析Html页面
		Elements form=doc.select("#formloginframe");//获取登录表单并且填写账号密码
		form.select("#loginname").val(loginname);
		form.select("#nloginpwd").val(loginpwd);
//		form.select("#loginpwd").val(loginpwd);

		Map<String,String> formData=HtmlUtils.formData(form);//构造登录表单数据
		formData.put("eid", "N3R3XCGF5F2NFMFGASRV5VQTOJLPQI2RH5WAC4NG4TGKFQKJYDVDP32N36OIHQCHNAYQP3RYAVXRQU43HD2EBUBMFA");
		formData.put("fp","92304845dcf7cf5a7d191ad17bd28f71");//js构造的表单参数
		String uuid=form.select("#uuid").val();
		//https://passport.jd.com/common/loginService?nr=1&uuid=5d707e97-baf6-4eb1-b3cc-5a9eeb263537&from=media&ReturnUrl=https%3A%2F%2Fmedia.jd.com%2FloginJump&r=0.12201340100727975
		String url=LoginUrl+"?nr=1&uuid="+uuid +"&from="+"media"+"&ReturnUrl="+"https%3A%2F%2Fmedia.jd.com%2FloginJump"+"&r="+Math.random();//登录请求地址
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){
			logger.error(e.getMessage());
		}
		HttpRequest postLoginUrlReq=HttpUtils.request(url, METHOD_POST);
		String cookie=HtmlUtils.toCookieValue(cookies);
		Map<String,String> headers=new HashMap<>();
		headers.put("Cookie", cookie);
		//以下请求头不会影响登录
		//headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		//headers.put("X-Requested-With", "XMLHttpRequest");
		//headers.put("Referer", LoginPage);
		//headers.put("Accept", "text/plain, */*; q=0.01");
		//headers.put("Accept-Encoding", "gzip, deflate");
		//headers.put("Accept-Language", "zh-CN,zh;q=0.8");
		postLoginUrlReq.headers(headers).form(formData);
		String body2=postLoginUrlReq.body();
		logger.info("登录请求的响应结果为{}",StringUtilsJD.decodeUnicode(body2));
		System.err.println("响应结果："+body2);
		if(!LoginSuccess.equals(body2)){
			throw new LoginException("登录失败，登录返回数据为："+body2);
		}
		Map<String, List<String>> responseHeaders2=postLoginUrlReq.headers();
		List<String> cookies2=responseHeaders2.get("Set-Cookie");
		logger.info("登录响应的cookie为{}",cookies2);
		return cookies2;
	}
	
	
	
	public static void main(String[] args) {
		List<String> cookies=login("13810259504", "tianke111111@");
		Order order=new Order();
		String cookie=HtmlUtils.toCookieValue(cookies);
		int year=2016;//订单时间
		System.out.println(order.getUserOrder(cookie, year));
	}
}
