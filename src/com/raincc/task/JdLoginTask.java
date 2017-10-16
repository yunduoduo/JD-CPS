package com.raincc.task;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_GET;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_POST;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.github.kevinsawicki.http.HttpRequest;
import com.jfinal.log.Logger;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.jd.core.jdutils.HtmlUtils;
import com.raincc.robot.jd.core.jdutils.HttpUtils;
import com.raincc.robot.jd.core.jdutils.StringUtilsJD;
import com.raincc.robot.jd.core.jdutils.TUtils;
import com.raincc.robot.util.HttpRequestUtils;
public class JdLoginTask implements Job {
	
	private static final Logger _log = Logger.getLogger(JdLoginTask.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		_log.info("JdLoginTask:每1小时一次登录更新");
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		int houses = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if(minute == 0 && houses % 3 == 0){
			Map<String,String> maps = TUtils.getCookieToLogin(TConstants.T_LOGINNAME, TConstants.T_PASSWORD);
			String code = maps.get("code");
			if("0".equals(code)){
				_log.info("登录更新成功");
			}else {
				_log.info("登录更新失败，原因是："+maps.get("info"));
			}
		}
		
	}
	public static Map<String, String> getCookieToLogin(String loginname ,String password){
		String pty = test1(loginname,password);
		if(StringUtils.isNotBlank(pty)){
		}else {
			return null;
		}
		test2(loginname,password);
		
//		if(true){
//			return null;
//		}  
		
		HttpRequest getLoginPageReq=HttpUtils.request(TConstants.JD_LoginPage, METHOD_GET);//请求登录页面
//		HttpRequest getLoginPageReq=HttpUtils.request("https://media.jd.com/loginJump", METHOD_GET);//请求登录页面
		String body=getLoginPageReq.body();
		System.err.println(body);
		Map<String, List<String>> responseHeaders = getLoginPageReq.headers();
		List<String> cookies=responseHeaders.get("Set-Cookie");//获取JSEESIONID
		_log.debug("请求登录页面响应结果为{}"+body);
		Document doc=Jsoup.parse(body);
		String cookie=HtmlUtils.toCookieValue(cookies);
		Elements form=doc.select("#formlogin");//获取登录表单并且填写账号密码
		form.select("#loginname").val(loginname);
		form.select("#nloginpwd").val(password);
		form.select("#nloginpwd").val(password);
		form.select("#authcode").val("");
		
//		form.select("#eid").val(TConstants.T_JDLOGIN_EID);
//		form.select("#sessionId").val(TConstants.T_LDLOGIN_FP);
		Map<String,String> formData=HtmlUtils.formData(form);//构造登录表单数据
//		formData.put("eid", "YRQJ6VQPYVTJ4IW4NOFUQVH7NVJNYWJWNB43A3A7GZWHNHX4N6CBNI5QPTOQFMTLVJHKUFJVYAKBYGLVHCIYKXGBPI");
//		formData.put("fp","f82706c4773a6459ab2d6aafee81cf3c");//js构造的表单参数
		String uuid=form.select("#uuid").val();
		String url=TConstants.JD_LoginUrl+"?uuid="+uuid+"&r="+Math.random()+"&version=2015";//登录请求地址
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
			_log.error(e.getMessage());
		}
		HttpRequest postLoginUrlReq=HttpUtils.request(url, METHOD_POST);
		Map<String,String> headers=new HashMap<>();
		headers.put("Cookie", cookie);
		
		postLoginUrlReq.headers(headers).form(formData);
		String body2=postLoginUrlReq.body();
		_log.info("登录请求的响应结果为{}"+StringUtilsJD.decodeUnicode(body2));
		Map<String, String> map  = new HashMap<String,String>();
		if(!TConstants.JD_LoginSuccess.equals(body2)){
			map.put("code","-1");
			map.put("info", body2);
			return map;
		}
		Map<String, List<String>> responseHeaders2=postLoginUrlReq.headers();
		List<String> cookies2=responseHeaders2.get("Set-Cookie");
		_log.info("登录响应的cookie为{}"+cookies2);
		String coo = HtmlUtils.toCookieValue(cookies2);
		map.put("code", "0");
		map.put("info",coo);
		return map;
	}
	private static void test2(String loginname, String password) {
		String urlString = "https://passport.jd.com/uc/showAuthCode?t="+Math.random()+"&version=2015";
//		HttpRequest teHttpRequest=HttpUtils.request(TConstants.JD_LoginPage, METHOD_GET);//请求登录页面
		String ContentType = "application/x-www-form-urlencoded; charset=utf-8";
		net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
//		jsonObject.put("ContentType",ContentType);
		jsonObject.put("Host","passport.jd.com");
		jsonObject.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
		jsonObject.put("Accept","text/plain, */*; q=0.01");
		jsonObject.put("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		jsonObject.put("Accept-Encoding","gzip, deflate, br");
		jsonObject.put("Referer","https://passport.jd.com/new/login.aspx");
		jsonObject.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
		jsonObject.put("X-Requested-With","XMLHttpRequest");
		jsonObject.put("Connection","keep-alive");
		jsonObject.put("Cache-Control","max-age=0");
		jsonObject.put("Cookie","__jda=122270672.15034541326891792955839.1503454133.1503632831.1503642256.9; __jdv=122270672|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_2fe4ad159a414f79ad013d0007ed4954|1503540227483; __jdu=15034541326891792955839; mp=bailingclub; TrackID=1zVkYasQ378T4b76egK-ZmJLfJzea1ddFyk1hwmZhDgPW3aXgdmcFHJXa0iDEsFZA0TlwRi1ouEZkFb0gCP8lfYWThMg5PEzDBxmMzY3flLs; pinId=D48GhzVhaXqopRlADUeaYg; _tp=NjawBw4ZvMB55W436wGWZQ%3D%3D; _pst=bailingclub; unpl=V2_ZzNtbUEAFxAlXUZQc0lZBWJUFFUSAkIVd11HVXofCFBiCxZYclRCFXMUR11nGlkUZwIZXEdcQBdFCHZXchBYAWcCGllyBBNNIEwHDCRSBUE3XHxcFVUWF3RaTwEoSVoAYwtBDkZUFBYhW0IAKElVVTUFR21yVEMldQl2VH8YXgBjBhJfQVBKHXUOTldzHVgMZQsibUVncyVxCERUchFsBFcCIh8WC0QXdghGUTYZWARlBhZYQlVAEnwARlJzGlQBYwoQVXJWcxY%3d; login_c=3; unick=bailingclub; login_m=1; 3AB9D23F7A4B3C9B=YRQJ6VQPYVTJ4IW4NOFUQVH7NVJNYWJWNB43A3A7GZWHNHX4N6CBNI5QPTOQFMTLVJHKUFJVYAKBYGLVHCIYKXGBPI; alc=qd/15nsTHLSlLtgLZYk3jw==; _ntvEeFQ=4dTi92TEfkTgh19V4n8Eo+Zg35yi6+BRfqIOpzWPLBk=; __jdc=122270672; _ntJjyGQ=GA0pjtZ5YSiq6nkbY+BdIjL/saIJLEvUSkmyWGZ5Xrs=; _ntvbzAg=8EmUJhqmMRMzLY7C0GDA8wclibQ9ujw6cmM4sSI/jc4=; _ntADzTo=CwV1W103Fi+P4/aSAALcTT8RwZ2tDRyjxCPVwqbTdtQ=; _ntCPyHD=ZfuFQBxxZuRFLNgYHadcaCLi0SmTQVHFK6RyYd6zk+A=; _ntzCncv=cVk1gdOv2cBtYFQqfR6tJdF6+HKobO4oUyK/XLyelns=; _ntVyZJn=Rdc4s5PKsFUZRS1IAwmcKQoXWyV74hQWPxpc0T1pLc0=; _ntTKbBu=+t5kCU+/ZkM24a26RxcoIDNMVVuyvnORYnwZJ7NuJsM=; qr_t=f; _nthHEmK=WeAmRy9p1bmBNAQv+W9kbX5UvezFAXy2qitbclNcZXk=; wlfstk_smdl=kmf8gvq11wtwldwjfvgol8htbevukvu5; _jrda=2; _ntjSWLg=aJb1yeLV0+FuL95+c/hZqtwNOWmX71HICNtUX6DGPPU=; _ntPxPcn=FWFjSHCdZzdJqAE18gtw9GuOved7EXTPadIrN4/3qjQ=; __jdb=122270672.2.15034541326891792955839|9.1503642256; _jrdb=1503642263099; _ntQvCHB=JXXsYHsC49sZc9KGWn5dBlvcJwOleYE5L95mhJptWTc=");
		jsonObject.put("loginName","bailingclub");
		String bodyss=HttpRequestUtils.sendHttpUrl(urlString, jsonObject.toString(), "", ContentType);
//		HttpRequest code=HttpUtils.request(urlString, METHOD_POST);//请求登录页面
//		code.headers(jsonObject);
//		String bodyss=code.body();
		System.err.println(bodyss);
		
		
	}
	private static String test1(String loginname, String password) {
		Map<String, String> jsonObject = new HashMap<String,String>();
		jsonObject.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
//		jsonObject.put("Host","passport.jd.com");
//		jsonObject.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
////		jsonObject.put("Accept","text/plain, */*; q=0.01");
//		jsonObject.put("Accept","*/*");
//		jsonObject.put("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//		jsonObject.put("Accept-Encoding","gzip, deflate, br");
//		jsonObject.put("Referer","https://passport.jd.com/new/login.aspx");
////		jsonObject.put("X-Requested-With","XMLHttpRequest");
//		jsonObject.put("Origin","https://passport.jd.com");
//		jsonObject.put("Connection","keep-alive");
//		jsonObject.put("Cache-Control","max-age=0");
//		jsonObject.put("Cookie","__jda=122270672.15034541326891792955839.1503454133.1503632831.1503642256.9; __jdv=122270672|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_2fe4ad159a414f79ad013d0007ed4954|1503540227483; __jdu=15034541326891792955839; mp=bailingclub; TrackID=1zVkYasQ378T4b76egK-ZmJLfJzea1ddFyk1hwmZhDgPW3aXgdmcFHJXa0iDEsFZA0TlwRi1ouEZkFb0gCP8lfYWThMg5PEzDBxmMzY3flLs; pinId=D48GhzVhaXqopRlADUeaYg; _tp=NjawBw4ZvMB55W436wGWZQ%3D%3D; _pst=bailingclub; unpl=V2_ZzNtbUEAFxAlXUZQc0lZBWJUFFUSAkIVd11HVXofCFBiCxZYclRCFXMUR11nGlkUZwIZXEdcQBdFCHZXchBYAWcCGllyBBNNIEwHDCRSBUE3XHxcFVUWF3RaTwEoSVoAYwtBDkZUFBYhW0IAKElVVTUFR21yVEMldQl2VH8YXgBjBhJfQVBKHXUOTldzHVgMZQsibUVncyVxCERUchFsBFcCIh8WC0QXdghGUTYZWARlBhZYQlVAEnwARlJzGlQBYwoQVXJWcxY%3d; login_c=3; unick=bailingclub; login_m=1; 3AB9D23F7A4B3C9B=YRQJ6VQPYVTJ4IW4NOFUQVH7NVJNYWJWNB43A3A7GZWHNHX4N6CBNI5QPTOQFMTLVJHKUFJVYAKBYGLVHCIYKXGBPI; alc=qd/15nsTHLSlLtgLZYk3jw==; _ntvEeFQ=4dTi92TEfkTgh19V4n8Eo+Zg35yi6+BRfqIOpzWPLBk=; __jdc=122270672; _ntJjyGQ=GA0pjtZ5YSiq6nkbY+BdIjL/saIJLEvUSkmyWGZ5Xrs=; _ntvbzAg=8EmUJhqmMRMzLY7C0GDA8wclibQ9ujw6cmM4sSI/jc4=; _ntADzTo=CwV1W103Fi+P4/aSAALcTT8RwZ2tDRyjxCPVwqbTdtQ=; _ntCPyHD=ZfuFQBxxZuRFLNgYHadcaCLi0SmTQVHFK6RyYd6zk+A=; _ntzCncv=cVk1gdOv2cBtYFQqfR6tJdF6+HKobO4oUyK/XLyelns=; _ntVyZJn=Rdc4s5PKsFUZRS1IAwmcKQoXWyV74hQWPxpc0T1pLc0=; _ntTKbBu=+t5kCU+/ZkM24a26RxcoIDNMVVuyvnORYnwZJ7NuJsM=; qr_t=f; _nthHEmK=WeAmRy9p1bmBNAQv+W9kbX5UvezFAXy2qitbclNcZXk=; wlfstk_smdl=kmf8gvq11wtwldwjfvgol8htbevukvu5; _jrda=2; _ntjSWLg=aJb1yeLV0+FuL95+c/hZqtwNOWmX71HICNtUX6DGPPU=; _ntPxPcn=FWFjSHCdZzdJqAE18gtw9GuOved7EXTPadIrN4/3qjQ=; __jdb=122270672.2.15034541326891792955839|9.1503642256; _jrdb=1503642263099; _ntQvCHB=JXXsYHsC49sZc9KGWn5dBlvcJwOleYE5L95mhJptWTc=");
		String url1 = "https://payrisk.jd.com/fcf.html?g=7TJI7TceJhZPW4NdFgEj7Tce7TZ37TceZLZPwB4kOg6eZTiBZlfQFTPGZHbQigAjZBFHZH6kwlAPwHcPw4wPwH7XWQPdygDPwHcPwj<PwH7*ieAewGAe6eAewQxbzQJkigJP7Tce7TZ37TceqQaC6jDPwHcPw4wPwH7XWeAewGAB6SAewdJpzQRXJtwPwHcPw4wPwH7XWkFPWdZpzlDPwHcPwj<PwHcE7Tce7T7L7Tceid7XJtZPWGAewGAB6SAew4FpWQfQztaPwHcPw4wPwH7GWQ9tWlfefQfeWlPXzGAewGAB6SAewHAevH2PwHcPw4wPwH7HzlxXW4RPW1Ro7Tce7TZ3wH6Pw4wPwH7Bit7PFgESFhZXz1fjyg9u7Tce7TZ37TceZBiDqL<BZHiPwHcPw4wPwH7jygkPqQ9uFA9QFdZPJIAewGAB6SjD7T7L7TceWlfBWlPXzPZjzt7bFlAPwHcPwjNjWdfP7T7L7Tcez09HigxTJ09eigJP7Tce7TZ3J17kFSAe6eAewQPuF0fDFgR<iGAewGAB6hReJgAPw4wPwH7bF0RIFgbbJQPXWGAewGAB6gFbz1ZP7T7L7Tcezt3Pz4RbJ0NGihZP7Tce7TZ3FQNsWlAPw4wPwH7HW1APwHcPwj<PwH7kzQCuztJu7Tce7T7L7TceW0xbJ0FXWQjPwHcPwj<PwH7hygDBwGAewGAe6eAewdReigZr7Tce7TZ37TceJgEBW0fHygFpFg6PwHcPw4wPwH7*z1fdygEB7Tce7TZ37TceJgE4FgFpzQf47Tce7T7L7TceilNuJQNB7Tce7TZ37TceigADil6kZHJ4OL7HiBRQiTieZH2jOL2*FgNQZT7QiBAPwHcPw4wPwH7tFg7dz<F*7Tce7TZ37TcewQ7biT7HZgFPZB2EZBceFTRbZlwtZQZHZT4*iQ6BwQcPwHcPw4wPwH7QygEdFh7*WQPuJIAewGAB6SAewQiDwHW*ZQwjZBWBiTijZTPbiH74ZQNbFQfPOLNHFHZH7Tce7T7L7TceFgP47Tce7TZ37Tcegf7RSHFgAf3FfPRKZ<PhZ<E5RPfRf4atTPFKTPPhSPJO6H6B6TZ3ZjJyfjbOSNajTHFL64E7ZfN6f<9RR4kATNFKS<CfR4pggANv6PP1TNFc6jPFSkb16P377Tce7TJ</&a=7TJI7TceW0Pu7Tce7TZ37Tce7Tce7T7L7TcezlP47Tce7TZ37Tce7Tce7T7L7TceWIAewGAB6SAewdwPwHcPw4wPwH7QWIAewGAB6SAewQiDwHW*ZQwjZBWBiTijZTPbiH74ZQNbFQfPOLNHFHZH7Tce7T7L7TcezeAewGAB6SAewd3bWtZ*zt7jvQp4vQZXzSAeRQEPJeAeRQxXFlPuvQNBW1aPwHcPw4wPwH7QieAewGAB6SAewPPSAAolfPN6gfFASHR7fBROTjFfAfFcZjEgS4EFfjphT4cjwj<B6TJ1gPJcT4biZ<Dl6j7OSTfRANR5AAFZf<xgS4bvfAFKfPP3Sj7FRjxgS<Z7gACiRj76SSAewGAe6eAewd6PwHcPwj<PwH7cTANIZAp0TPf<SNPg6BFNZ4C76BFifBRTZfZwfkbiZBR3T<95RNP6SkbvSTWkR<PZfLFvAkZ<ZH7ORfckg<PSZ4p3SNZIfAJNRkJFTeAewGAtR2/";
		Map<String, String> jo = new HashMap<String, String>();
		jo.put("d","7TJI7Tceil<PwHcPwj<PZjcPwH7jF<bbWlaPwHcPwj<kwHAjwL2lZBWPw4wPwH7HzlEjFhbjTQNCFSAewGAB6SAewdJPiQJs7T7LFhb*Fh7pzgfuJ0NsvhJPiQJs7Tce7T7L7TceJlfGFlxlFh7Byg9u7Tce7TZ37TceflfGRj*PwH2xvH2PwHcPw4wPwH7By0N4ygEdTNiPwHcPwj<PwH7hFg71TIAew<JwAj*PwH3NAeAewL<uwIAewGAe6eAewdFPzQRXWGAewGAB6SAew4kXqQPsz0<PwHcPw4wPwH7eFgE4Fh7PWGAewGAB6SAew4kXqQPsz0<PwHcPw4wPwH7Pq1RPzdZpzlEB7Tce7TZ37TfI7Tce6AE1T<fVygEBJ0Nuilf4hlNeWQNEWeAewGAe6eAew4fifN9Gz0fuFN9CygECihaPwHcPw4wPwH7NgNRVil9szt7VidfQFQfehlbbz0FVFQxXih6PwHcPw4wPwH7NgNRVFd7bFk94Fh3jyIAewGAe6eAew4fifN9By0N4Fh7VJ0fDJ1feFf9szl6PwHcPw4wPwH7NgNRVJ0fDJ1feFf9QygxjFh7VigEpWl9jWQ9*ygwPwHcPw4wPwH7NgNRVF0PByQ9pzdRVJ0PCFh7VWhfPWd4PwHcPw4wPwH75RfZVFgxPzgfuJN9pzQRPqN9kygEj7Tce7T7L7TceTjfThtZjigE4ih74hlRPWQPlihRpJQfB7Tce7T7L7TceTjfThtRPq1RkWQfVFQxXih6PwHcPw4wPwH75RfZVJ0fDJ1feFf9Qz09bJN9sygEPihcPwHcPw4wPwH75RfZVJ0fDJ1feFf9oigxQhlFszlNj7Tce7T7L7TceTjfThtRPq1RkWQfVy0NsFP9Qz09bJN9sygEPihcPwHcPw4wPwH75RfZVJQfeJ0fDhlNeWQNEhl9GyQfHJIAewGAe6eAewPJN64JwhlZXz09ehl7kFQFPWP9Qz09bJIAewGAe6eAewPJN64JwhlZXzh3eFhZBFgRVJ0fDJ1feFf9PJ0wx7Tce7T7L7TcefjfIRjxVil9CW17PWtZPFN9jFhbjJh7PhtwBJ0wPwHcPw4wPwH7hRA71TN94Fh3jyN9jFhbjJh7P7Tce7T7L7TcefjfIRjxVF17bJk9GJgFQFh7B7Tce7T7L7TcefjfIRjxVz09BFf9HzlEjFhbj7Tce7T7L7TceTA9yhkJN64JwhlxXWlfVil9uJ0fDJIAewGAe6eAew4k5gP9hRA71TN9Hzlk*WQfBWlf4htRPq1RkWQfVWBZjieAewGAe6eAew4k5gP9hRA71TN94Fh3jyN9jFhbjJh7P7Tce7Tf<7TJ<7T7L7TceJ1wPwHcPwj<PZjcPwH74FhFpilfAygkP7Tce7TZ3wTA*wBijwB4tZLakZGAtRIAe6eAewQjPwHcPwj<PZjcPwH7Hzlk*ihRZzlRP7Tce7TZ37Tce6kZTwAZXzh3bJIAewGAtRIAe6eAewQFX7Tce7TZ37TfI7Tce6h7biQPH7Tc*f1P*FhZPJ1RpzQWPwHcPw4wPwH7IihRbzQWPwHcPw4wPwH7IihfoihfB7Tc*OTwPwHcPw4wPwH7Ligxpid7p7Tce7T7L7TceR<Fvig4CAjcPwHcPw4wPwH7<ztRkzSAewGAe6eAew4FbzQJTzlEd7Tce7T7L7TceRlNGWQPXz0<PwHcPw4wPwH71yhZoiSAewGAe6eAew4Jkz0PC7Tce7T7L7TceSgk*igZj7Tce7T7L7TceTgNsFtfu7Tc*Rl9jy0PH7Tce7T7L7TceTgPuFjxpfSkNq1RI7Tce7T7L7TceTg9Xz<7XWQNu7Tce7T7L7TceTfwPwH36TgPuilbX7Tce7T7L7TceTdPbz0<PwHcPw4wPwH76TgPuFjxpfSAewGAe6eAewPZPFl9P7Tc*A17pzd6PwHcPw4wPwH7TygkcFg4PwHcPZA6Pw4wPwH7u7Tce7TZ37TJI7TceyQNliAfuig7sFg6PwHcPwjNQigxBFSAe6eAewdRbygEjRgEbiQxPFIAewGAB6gFbz1ZP7T7L7TceF09OztRAWQNHyeAewGAB6SAewdfuWt3PilPQygf47Tce7T7L7TceztZHW1APwHcPwj<PwH7hygE4ztJB7Tc*TP6PwH2lvH<PwjcPwH3hTkWlZIAewGAe6eAewdFPzQRXWGAewGAB6SAewGAewGAe6eAewdFPzQRXWPZkiGAewGAB6SAewGAewGAe6eAewd3ezlRkitRTJgcPwHcPwj<PwHcewL<*wL<*wSAewGAe6eAewQZXzlCpFAfuig7sFg6PwHcPwjNjWdfP7T7L7Tceidfpz0R7RIAewGAB6SAewHc*wTW*wB2ewTc*ZBAx7Tce7T7L7Tcey0NeF1JbWQfLzlEHJh7eFgEHqSAewGAB6T6Pw4wPwH7bW13LzlRPTQNCFSAewGAB6SAew4kXqQPsz0<PwHcPw4wPwH7bW13OigkP7Tce7TZ37TceTQfjWlZbW0APwHcPw4wPwH7bW13gFh7Byg9u7Tce7TZ37TceZSD*7Tc*KNJpzQRXJtwp7Tce7T7L7TceW0xbJ0FXWQjPwHcPwj<PwH7hygDBwGAewGAe6eAewdfBFh73FlfuJIAewGAB6SAew4kXqQPsz0<Pw4ikvH2PwH2oflPuF09tWeAew<EA7Tc*ZGDx7TZI7Tc*fj9hZH6PwjcPwH3eJGAB6TAevH2p7Tc*RlfHyl8Pw4iewL<*wL<*wSAew<FpWQfQztaPw4ikwGD*7Tce7T7L7TceW17XF1fHJIAewGAB6SAew4JPilCX7Tce7T7L7Tcez0NuFtfbFlAPwHcPwj<PwH7myIkLTGAewGAe6eAewQ9uT0PuFSAewGAB6hReJgAPw4wPwH7PzdfCFh7bJ0PXz49eF0fe7Tce7TZ37TfI7TceJQPGWQNjFSAewGAe6eAewQpbJQNNzQNGz0f47Tce7T7L7TceFlfjRlNCFh3bF1wPwHcPw4wPwH7Cztp1FhRfWlfeTgf4yg<PwHcPw4wPwH7BFgE46Qfbil9u7Tce7T7L7TceWQfxJgfBJ<kPF0PbSlfEAtPBJ0fC6gZHFhZB7Tce7T7L7TceWQfdyhZjFh76WQ9jzlZXz<bbzQRsFhcPwHcPw4wPwH7eFgJpWtRPW4ZXzdRPzdRcigE4z0fe7Tce7T7L7TceJ0NpzdRNzQNGz0f47Tce7T7L7TceW0fezgPBWlPXzdwPwHcPw4wPwH7CygkPf1P*FhwPwHcPw4wPwH7*z1fdygEB7Tce7T7L7TceF09OztRAWQNHyeAewGAe6eAewQ9Bit3k7Tce7T7L7TceJQfuF09e7Tce7T7L7TceJQfuF09eAtfG7Tce7T7L7TceW17XF1fHJNZkiGAewGAe6eAewQZXzlCpFAfuig7sFg6PwHcPw4wPwH7GJgPsF<P<7Tce7T7L7Tcezgf4ygN<FhFpilfB7Tce7T7L7TceWlfeJQPHFfJXWQCPWGAewGAe6eAewQbbWQRtih7P6l9uitfeWQfuit4PwHcPw4wPwH7dFg9szlZbJ0PXzGAewGAe6eAewQN*W<ZXF0fOigkP7Tce7T7L7Tceih3*TQNCFSAewGAe6eAewQN*WNFPWdZpzlDPwHcPw4wPwH7*z0NjFQ9ezSAewGAe6eAewdfBFh73FlfuJIAewGAe6eAewd3ezlRkit6PwHcPw4wPwH7sigEdJgNdFSAewGAe6eAewQxbzQJkigJPWeAewGAe6eAewQ9uT0PuFSAewGAkRIAtRIAe6eAewd2PwHcPwj<PZAcPZA6Pw4wPwH7t7Tce7TZ37TJI7TceF0flygZPA0PDFgxSihRpzeAewGAB6T<PZj6Pw4wPwH7B7Tce7TZ37TJI7TceihFbygxcFgPdy16PwHcPwj<twHaPw4wPwH7bJQNpzNJpF1Ro7Tce7TZ3wTwlZGAe6eAewQZXz09eR0f*J0aPwHcPwj<eZIAe6eAewQbPygJoJIAewGAB6TWlOIAe6eAewdJpF1Ro7Tce7TZ3wTwlZGAe6eAewd3pq0fsR0f*J0aPwHcPwj<eZIAtRIAe6eAewdZH7Tce7TZ37TJI7Tce6gZjyhFP6Q9eF0fe7Tce7TZ37TceWQJGKL<DwIAe6eAewL<DwIAe6eAewL<DwI4PwHcPw4wPwH73itRpJQfLih3jyg9u7Tce7TZ37TceWQJGKL<kweAe6eAewL<DwIAe6eAewLc*OS4PwHcPw4wPwH73W13hzt7rWt3bilAPwHcPwj<PwH7eFlcowTWx7T7L7Tc*wTWx7T7L7Tc*wTWxKSAewGAe6eAew47bilCdWQ9kzQ6PwHcPwj<PwH7eFlcowIAe6eAewL2Pw4wPwH2*KSAewGAe6eAew47kJ1RXz4FbilAPwHcPwj<PwH7eFlcowH6*7T7L7Tc*wH6*7T7L7Tc*wH6*KSAewGAe6eAew47kJ1RXz4bpFlbsygJoJIAewGAB6SAewd7diGaewBAPw4wPwH2eZL<Pw4wPwH2eZT2p7Tce7T7L7Tce6dfjJ09uAlbbF09t7Tce7TZ37TceWQJGKL<lwIAe6eAewL<lwIAe6eAewL<lwI4PwHcPw4wPwH7IJhRjzlEAFhbj7Tce7TZ37TceWQJGKL2Pw4wPwH2*7T7L7Tc*wI4PwHcPw4wPwH7Lih3jyg9uf0fDJIAewGAB6SAewd7diGa*7T7L7Tc*wIAe6eAewL2p7Tce7T7L7TceRt7bqfRPq16PwHcPwj<PwH7eFlcowT2E7T7L7Tc*wT2E7T7L7Tc*wT2EKSAewGAe6eAew4bpFlbsygJoJIAewGAB6SAewd7diGajweAe6eAewL<BOIAe6eAewL<jOS4PwHcPw4wPwH7cygJoz0Pdy1RAFhbj7Tce7TZ37TceWQJGKLckZSAe6eAewLckZSAe6eAewLckZS4PwHcPw4wPwH77zQNHJ0PlFA7XWQRPWGAewGAB6SAewd7diGaeZL6Pw4wPwH2eZLWPw4wPwH2eZTcp7Tce7T7L7TceSgEbitRpJQfLih3jyg9u7Tce7TZ37TceWQJGKL<EwSAe6eAewLc*ZSAe6eAewLcxOS4PwHcPw4wPwH77zQNHJ0PlFAZbW1RpzlEAFhbj7Tce7TZ37TceWQJGKLit7T7L7Tc*ZBaPw4wPwH2DZI4PwHcPw4wPwH77zQFX6QNHylJeztfuFIAewGAB6SAewd7diGaeZTAPw4wPwH2eZTAPw4wPwH2ewHAp7Tce7T7L7TceSgEQzkRPq16PwHcPwj<PwH7eFlcowIAe6eAewL2Pw4wPwH2*KSAewGAe6eAew4kPzdAPwHcPwj<PwH7eFlcowH6*7T7L7Tc*wH6*7T7L7Tc*wH6*KSAewGAe6eAew4kPzdfAFhbj7Tce7TZ37TceWQJGKL2Pw4wPwH2*7T7L7Tc*wI4PwHcPw4wPwH7Tit7Xz0xGihcPwHcPwj<PwH7eFlcowH2*7T7L7Tc*wH2*7T7L7Tc*wH2*KSAewGAe6eAewPRoWQfPR<RbWQCTy0N4ztWPwHcPwj<PwH7eFlcowT2k7T7L7Tc*wT2k7T7L7Tc*wT2kKSAewGAe6eAewPRoWQfPR<FbilAPwHcPwj<PwH7eFlcowH6*7T7L7Tc*wH6*7T7L7Tc*wH6*KSAewGAe6eAewPRoWQfPR<bpFlbsygJoJIAewGAB6SAewd7diGaewBAPw4wPwH2eZL<Pw4wPwH2eZT2p7Tce7T7L7Tcef0beFgf<T0Pdy1RTy0N4ztWPwHcPwj<PwH7eFlcowHct7T7L7Tc*wHct7T7L7Tc*wHctKSAewGAe6eAewPRoWQfPRNZoigRXJeAewGAB6SAewd7diGaxZH2Pw4wPwH2xZH2Pw4wPwH2xZH2p7Tce7T7L7TceflPuF09t7Tce7TZ37TceWQJGKL<EOSAe6eAewLcBZeAe6eAewLc*ZI4PwHcPw4wPwH7hygE4ztJ0WQNCFSAewGAB6SAewd7diGaxwL2Pw4wPwH2xwL2Pw4wPwH2xwL2p7Tce7T7L7TceflPuF09tf0fDJIAewGAB6SAewd7diGa*7T7L7Tc*wIAe6eAewL2p7Tce7TJ<7T7L7TceJ1oPwHcPwj<CZLa*7T7L7Tcez0Ps7Tce7TZ37Tce7Tce7T7L7TceJlPs7Tce7TZ37Tce7Tce7T7L7TceWtwPwHcPwj<PZjcPwH7Hzl9rygAPwHcPwjNjWdfP7T7L7Tcez09HigxTJ09eigJP7Tce7TZ3J17kFSAe6eAewdZPWtZpzlETJ09eigJP7Tce7TZ3J17kFSAe6eAewQJszl7bzNZjzt7bFlAPwHcPwjNQigxBFSAe6eAewQPuF0fDFgR<6GAewGAB6hReJgAPZj6Pw4wPwH7QzIAewGAB6gEkz0*PZj6/");
//		String bodyss=HttpRequestUtils.sendHttpUrl(url1, jo.toString(), "", ContentType);
		HttpRequest post1 = HttpUtils.request(url1, METHOD_POST);
		post1.headers(jsonObject).form(jo);
		String body1=post1.body();
		System.err.println(body1);
		return body1;
	}

}
