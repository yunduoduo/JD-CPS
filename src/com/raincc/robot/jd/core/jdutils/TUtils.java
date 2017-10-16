package com.raincc.robot.jd.core.jdutils;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_GET;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_POST;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.github.kevinsawicki.http.HttpRequest;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.JdImportOrders;
import com.raincc.robot.entity.JdPermission;
import com.raincc.robot.entity.WxJdUser;
import com.raincc.robot.util.HttpRequestUtils;
import com.raincc.robot.util.MD5;

public class TUtils {

	protected static final Logger _log = Logger.getLogger(TUtils.class);
	
	public static final SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	public static final SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getJdToken(Controller c) {
		String token = c.getCookie(TConstants.JD_ACCESS_TOKEN);
		if(StringUtils.isBlank(token)){
			JdPermission jp = JdPermission.dao.findFirst("select JD_AccessToken from rb_jdpermission where unionId = ? and tokenValidTime > UNIX_TIMESTAMP() ",TConstants.BL_JD_UNIONID);
			if(jp != null){
				if(StringUtils.isNotBlank(jp.getStr("JD_AccessToken"))){
					c.setCookie(TConstants.JD_ACCESS_TOKEN,jp.getStr("JD_AccessToken"),1 * 24 * 60 * 60);
					return jp.getStr("JD_AccessToken");
				}
			}
		}else {
			return token;
		}
		return null;
	}
	
	/**获取京东公共参数集
	 * @param token
	 * @param method
	 * @return
	 */
	/**
	 * https://api.jd.com/routerjson?
	 * v=2.0
	 * &method=jingdong.service.promotion.wxsq.getCodeBySubUnionId
	 * &app_key=&
	 * 360buy_param_json={"proCont":"","materialIds":"","subUnionId":"","positionId":""}
	 * &timestamp=2017-08-03 11:43:26&
	 * sign=8822793EDFA21EBB0CF51AA5C6311BA0
	 * {"jingdong_service_promotion_wxsq_getCodeBySubUnionId_responce"
	 * :{"code":"0","getcodebysubunionid_result":"{\"resultCode\":\"403\",\"resultMessage\":\"您无权限调用此API接口！\"}"}}
	 */
	public static String getJdParams(Long unionId,String access_token,String methodName,JSONObject netJoson){
		
		String _360buy_param_json = sortJsonObject(netJoson).toString();
		String sign = getJdSign(_360buy_param_json,access_token,methodName,TConstants.v);
		String urls = TConstants.JD_URL+"?"+
//				"jdUnionId="+unionId+
				"method="+methodName+
				"&v="+TConstants.v+
				"&app_key="+TConstants.JD_Appkey+
				"&access_token="+access_token+
				"&360buy_param_json="+_360buy_param_json+
				"&timestamp="+getDateYMdHms("yyyy-MM-dd HH:mm:ss",new Date()).replace(" ", "&nbsp;")+
				"&sign="+sign;
		HttpRequest requert=HttpUtils.request(urls, "GET");
		String body = requert.body();
//		_log.info("京东接口返回结果："+body);
		return body;
	}

	public static String getJdInterface(String access_token,String methodName,JSONObject netJoson){
		String _360buy_param_json = sortJsonObject(netJoson).toString();
		String sign = getJdSign(_360buy_param_json,access_token,methodName,TConstants.v);
		String urls = TConstants.JD_URL+"?"+
//				"jdUnionId="+2010539967+
				"method="+methodName+
				"&v="+TConstants.v+
				"&app_key="+TConstants.JD_Appkey+
				"&access_token="+access_token+
				"&360buy_param_json="+_360buy_param_json+
				"&timestamp="+getDateYMdHms("yyyy-MM-dd HH:mm:ss",new Date()).replace(" ", "&nbsp;")+
				"&sign="+sign;
		HttpRequest requert=HttpUtils.request(urls, "GET");
		String body = requert.body();
		requert.getConnection().disconnect();
//		String body = HttpRequestUtils.sendHttpUrl(urls, "", "", TConstants.ContentType_FORMDATA);
//		_log.info("京东接口返回结果："+body);
		return body;
	}
	/**京东签名
	 * @param _360buy_param_json
	 * @param access_token
	 * @param method
	 * @param v
	 * @return
	 */
	public static String getJdSign(String _360buy_param_json,
			String access_token, String method, String v) {
		String sin = TConstants.JD_AppSecret+"360buy_param_json"+_360buy_param_json
				+"access_token"+access_token+"app_key"+TConstants.JD_Appkey+"method"+method
				+"timestamp"+getDateYMdHms("yyyy-MM-dd HH:mm:ss",new Date())+"v"+v+TConstants.JD_AppSecret;
		String sinMd = MD5.Encryption(sin,32).toUpperCase();
		System.err.println("签名为："+sinMd);
		return sinMd;
	}
	
	/**net.sf.json 排序
	 * @param obj
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JSONObject sortJsonObject(JSONObject obj) {
	        Map map = new TreeMap();
	        Iterator<String> it = obj.keys();
	        while (it.hasNext()) {
	            String key = it.next();
	            Object value = obj.get(key);
	            if (value instanceof JSONObject) {
	                // System.out.println(value + " is JSONObject");  
	                map.put(key, sortJsonObject(JSONObject.fromObject(value)));  
	            } else if (value instanceof JSONArray) {
	                // System.out.println(value + " is JSONArray");  
	                map.put(key, sortJsonArray(JSONArray.fromObject(value)));  
	            } else {
	                map.put(key, value);
	            }
	        }
	        return JSONObject.fromObject(map);
	    } 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JSONArray sortJsonArray(JSONArray array) {
	        List list = new ArrayList();
	        int size = array.size();
	        for (int i = 0; i < size; i++) {
	            Object obj = array.get(i);
	            if (obj instanceof JSONObject) {
	                list.add(sortJsonObject(JSONObject.fromObject(obj)));  
	            } else if (obj instanceof JSONArray) {
	                list.add(sortJsonArray(JSONArray.fromObject(obj)));  
	            } else {
	                list.add(obj);
	            }
	        }
	        Collections.sort(list);
	        return JSONArray.fromObject(list);
	    }
	
	public static String getDateYMdHms(String yMdHms,Date date){
		return new SimpleDateFormat(yMdHms).format(date);
	}
	public static int getDays(int year, int month) { 
	    int days = 0;
	    if (month != 2) {
	        switch (month) {
	        case 1:
	        case 3:
	        case 5:
	        case 7:
	        case 8:
	        case 10:
	        case 12:
	        days = 31;
	        break;
	        case 4:
	        case 6:
	        case 9:
	        case 11:
	        days = 30;
	        }
	    } else {
	        //闰年
	        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
	        days = 29;
	        else
	        days = 28;
	    }
	    return days;
	 }
	public static Date getStringToDate(String sd){
		Date date = null;
		try {
			date = format.parse(sd);
		} catch (ParseException e) {
			e.printStackTrace();
			_log.info("时间格式转换失败");
		}
		return date;
	}
	
	public static Date getLongToDate(Long t){
		String d = format.format(t);
		Date date = null;
		try {
			date = format.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
			_log.info("时间格式转化失败。。。");
		}
		return date;
		
	}

	
	/**String 类型从多少日期到多少日期的集合
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<String> getStringDateList(String startDate,String endDate){
		List<String> list = new ArrayList<String>();
		Date sdDate = null;
		Date toddate = null;
		String upDate = startDate;
		try {
			sdDate = formatYMD.parse(upDate);
			toddate = formatYMD.parse(endDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
			_log.info("时间格式转换失败。。。");
			return null;
		}
		if(sdDate.getTime() > toddate.getTime()){
			return null;
		}
		while(true){
			try {
				if(endDate.equals(upDate)){
					list.add(upDate);
					return list;
				}else {
					list.add(upDate);
					upDate = getAfterDate(formatYMD.parse(upDate),1);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**计算从当天开始不算当天，往前或者后推算多少天的时间集合，yyyy-MM-dd
	 * @param BeginDays 往前或者后推算多少天,往前就是-30，day是正(前多少天开始推算)
	 * @param ToDays 往前或者后推算多少天,往前就是-30，day是正(推算到哪天为止，当天就是0)
	 * @param day 一次跳级几天，例如1天
	 * @return
	 */
	public static List<String> getAfterDateList(Integer BeginDays,Integer ToDays,Integer day){
		List<String> list = new ArrayList<String>();
		String sd = getAfterDate(new Date(),BeginDays);
		String tod = getAfterDate(new Date(),ToDays);
		while(true){
			try {
					sd = getAfterDate(formatYMD.parse(sd),day);
				if(tod.equals(sd)){
					return list;
				}else {
					list.add(sd);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	/**某一时间的前或者后多少天
	 * @param date
	 * @param days
	 * @return
	 */
	public  static String getAfterDate(Date date,Integer days){
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, days);//计算30天后的时间
		String str2=s.format(c.getTime());
		return str2;
	}
	
	/**
	 * 更新京东爬取数据
	 */
	public static void crawling(final String dateString) {
		synchronized (TUtils.class) {
			Integer count = 1;
			Integer conData = 0;
			Integer co = 0;
			String cookie = WxJdUser.dao.findFirst("select cookie from rb_wxjduser where unionId = ? ",TConstants.BL_JD_UNIONID).getStr("cookie");
			while (true) {
				try {
					JSONObject  data = new JSONObject();
					JSONObject j1 = new JSONObject();
					j1.put("total",0);
					j1.put("pageNum",count);
					j1.put("size",20);
					data.put("pagination", j1);
					data.put("order","[]");
					data.put("data", "[]");
					List<String>list = new ArrayList<String>();
					JSONObject j2 = new JSONObject();
					j2.put("name","orderStatus");//查询是否有效，有效value是1，全部值为空
					j2.put("value","");
					list.add(j2.toString());
					JSONObject j3 = new JSONObject();
					j3.put("name","accountDateStr");
					//String dateString = TUtils.getDateYMdHms("yyyy-MM-dd", new Date());
					j3.put("value",dateString+"#"+dateString);
					list.add(j3.toString());
					JSONObject shortcutDate = new JSONObject();
					shortcutDate.put("name","shortcutDate");//快捷时间，今天value=0，昨天是1，过去7天就是7
					shortcutDate.put("value","");
					list.add(shortcutDate.toString());
					JSONObject orderId = new JSONObject();
					orderId.put("name","orderId");
					orderId.put("value","");
					list.add(orderId.toString());
					data.put("search", list);
					String body = HttpRequestUtils.sendHttpUrl(TConstants.JD_PY_ImportOrders, data.toString(),cookie,TConstants.ContentType_PAYLOAD);
					if(StringUtils.isNotBlank(body) && !body.contains("<body")){
						JSONObject jsonObject = JSONObject.fromObject(body);
						JSONArray dataArray = jsonObject.getJSONArray("data");
						_log.info("结果有"+dataArray.size()+"数据。");
						conData += dataArray.size();
						if(dataArray.size() > 0){
							for (int i = 0; i < dataArray.size(); i++) {
								JSONObject jo = dataArray.getJSONObject(i);
									new JdImportOrders().crawling(jo);
							}
							count ++;
							Thread.sleep(1500);
						}else {
							_log.info("一共执行了"+conData+"数据。");
							return;
						}
					}else {
						if(co >= 1){
							_log.info("一共执行了"+conData+"数据。");
							return;
						}
						co++;
					}
				} catch (Exception e) {
					_log.info("一共执行了"+conData+"数据。抛出异常："+e);
					return;
				}
				
			}
		}
	}

	/**
	 * 更新一次京东api接口,一次最多一天的记录
	 */
	public static void impOrder(final String time) {
		synchronized (TUtils.class) {
			JSONObject netJoson = new JSONObject();
			Long unionId = TConstants.BL_JD_UNIONID;
			String token = JdPermission.dao.findFirst("select JD_AccessToken from rb_jdpermission where unionId = ? ",unionId).getStr("JD_AccessToken");
			netJoson.put("unionId",unionId);
			netJoson.put("time",time);
			netJoson.put("pageIndex",1);
			netJoson.put("pageSize",999999999);
			String body = TUtils.getJdInterface(token, TConstants.JD_METHOD_getQueryImportOrders, netJoson);
			if(body.contains("error_response") || StringUtils.isBlank(body)){
				return;
			}
			JSONObject responceBody = JSONObject.fromObject(body);
			JSONObject responce = responceBody.getJSONObject("jingdong_UnionService_queryImportOrders_responce");
			JSONObject jsonObject = responce.getJSONObject("queryImportOrders_result");
			JSONArray data = null;
			if(jsonObject.toString().contains("data")){
				data = jsonObject.getJSONArray("data");
				_log.info("JdSameDayOrderTask获取到"+data.size()+"数据。");
				for (int i = 0; i < data.size(); i++) {
					JSONObject j = data.getJSONObject(i);
						new JdImportOrders().saveOrder(j,unionId);
				}
			}
		
		}
	}
	
//	/**京东模拟登录获取cookie
//	 * @param loginname
//	 * @param password
//	 * @return
//	 */
//	public static Map<String, String> getCookieToLogin(String loginname ,String password){
//		HttpRequest getLoginPageReq=HttpUtils.request(TConstants.JD_LoginPage, METHOD_GET);//请求登录页面
//		String body=getLoginPageReq.body();
//		Map<String, List<String>> responseHeaders = getLoginPageReq.headers();
//		List<String> cookies=responseHeaders.get("Set-Cookie");//获取JSEESIONID
//		_log.debug("请求登录页面响应结果为{}"+body);
//		Document doc=Jsoup.parse(body);
//		String cookie=HtmlUtils.toCookieValue(cookies);
//		Elements form=doc.select("#formlogin");//获取登录表单并且填写账号密码
//		form.select("#loginname").val(loginname);
//		form.select("#nloginpwd").val(password);
//		Map<String,String> formData=HtmlUtils.formData(form);//构造登录表单数据
//		formData.put("eid", TConstants.T_JDLOGIN_EID);
//		formData.put("fp",TConstants.T_LDLOGIN_FP);//js构造的表单参数
//		String uuid=form.select("#uuid").val();
//		String url=TConstants.JD_LoginUrl+"?uuid="+uuid+"&r="+Math.random()+"&version=2015";//登录请求地址
//		try{
//			Thread.sleep(1000);
//		}catch(InterruptedException e){
//			_log.error(e.getMessage());
//		}
//		HttpRequest postLoginUrlReq=HttpUtils.request(url, METHOD_POST);
//		Map<String,String> headers=new HashMap<>();
//		headers.put("Cookie", cookie);
//		postLoginUrlReq.headers(headers).form(formData);
//		String body2=postLoginUrlReq.body();
//		_log.info("登录请求的响应结果为{}"+StringUtilsJD.decodeUnicode(body2));
//		Map<String, String> map  = new HashMap<String,String>();
//		if(!TConstants.JD_LoginSuccess.equals(body2)){
//			map.put("code","-1");
//			map.put("info", body2);
//			return map;
//		}
//		Map<String, List<String>> responseHeaders2=postLoginUrlReq.headers();
//		List<String> cookies2=responseHeaders2.get("Set-Cookie");
//		_log.info("登录响应的cookie为{}"+cookies2);
//		String coo = HtmlUtils.toCookieValue(cookies2);
//		map.put("code", "0");
//		map.put("info",coo);
//		Db.update("update rb_wxjduser set cookie = ? where unionId = ? and loginname = ? ",coo,TConstants.BL_JD_UNIONID,TConstants.T_LOGINNAME);
//		return map;
//	}
	
	
	
	/**京东模拟登录获取cookie
	 * @param loginname
	 * @param password
	 * @return
	 */
	public static Map<String, String> getCookieToLogin(String loginname ,String password){
		Map<String,String> maps = Jdlogin1(loginname, password);
		Map<String, String> map = new HashMap<String,String>();
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			map.put("code","-1");
			map.put("info","模拟登录线程异常。。。");
			_log.info("线程停留失败。。。");
			return null;
		}
		Map<String, String>map2 = Jdlogin2(maps, loginname,password);
		if("0".equals(map2.get("code"))){
			map.put("code", "0");
			map.put("info",map2.get("info"));
			Db.update("update rb_wxjduser set cookie = ? where unionId = ? and loginname = ? ",map2.get("info"),TConstants.BL_JD_UNIONID,TConstants.T_LOGINNAME);
		}else {
			map.put("code","-1");
			map.put("info",map2.get("info"));
		}
		return map;
		
	}
	public static Map<String, String> Jdlogin2(Map<String, String> map, String loginname,String password){
		Map<String, String>map2 = new HashMap<String,String>();
		String formlogin = map.get("formlogin");
		String cookie = map.get("jd_cookie");
		Document doc=Jsoup.parse(formlogin);//解析Html页面
		Elements form=doc.select("#formlogin");//获取登录表单并且填写账号密码
		form.select("#loginname").val(loginname);
		form.select("#nloginpwd").val(password);
		Map<String,String> formData=HtmlUtils.formData(form);//构造登录表单数据
		formData.put("eid", TConstants.T_JDLOGIN_EID);
		formData.put("fp",TConstants.T_LDLOGIN_FP);//js构造的表单参数
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
		postLoginUrlReq.headers(headers).form(formData);
		String body2=postLoginUrlReq.body();
		_log.info("登录请求的响应结果为{}"+StringUtilsJD.decodeUnicode(body2));
		if(body2.contains("success") && body2.contains("//www.jd.com")){
			Map<String, List<String>> responseHeaders2=postLoginUrlReq.headers();
			List<String> cookies2=responseHeaders2.get("Set-Cookie");
			_log.info("登录响应的cookie为{}"+cookies2);
			String cookieString = HtmlUtils.toCookieValue(cookies2);
			map2.put("code","0");
			map2.put("info",cookieString);
			return map2;
		}else {
			map2.put("code", "-1");
			map2.put("info",StringUtilsJD.decodeUnicode(body2));
			_log.info("自动模拟登录失败，"+StringUtilsJD.decodeUnicode(body2));
			return map2;
		}
		
	}
	public static Map<String, String> Jdlogin1(String loginname,String password){
			HttpRequest getLoginPageReq=HttpUtils.request(TConstants.JD_LoginPage, METHOD_GET);//请求登录页面
			String body=getLoginPageReq.body();
			Map<String, List<String>> responseHeaders=getLoginPageReq.headers();
			List<String> cookies=responseHeaders.get("Set-Cookie");//获取JSEESIONID
			String cookie=HtmlUtils.toCookieValue(cookies);
			_log.debug("请求登录页面响应结果为{}"+body);
			Document doc=Jsoup.parse(body);
			String urlString = "https://passport.jd.com/uc/showAuthCode";
			net.sf.json.JSONObject jsonObject = new net.sf.json.JSONObject();
			jsonObject.put("Content-Type", TConstants.ContentType_FORMDATA);
			jsonObject.put("r",Math.random());
			jsonObject.put("version",2015);
			jsonObject.put("loginName",loginname);
			
			String bodyss=HttpRequestUtils.sendHttpUrl(urlString, jsonObject.toString(), cookie, TConstants.ContentType_FORMDATA);
			_log.info("自动登录验证码验证结果为："+bodyss);
			Elements form=doc.select("#formlogin");
			Map<String, String>map = new HashMap<String,String>();
			map.put("formlogin",form.toString());
			map.put("jd_cookie",cookie);
			return map;
	}
	
	/**字符串前后颠倒
	 * @param s
	 * @return
	 */
	public static String reverse(String s){
		return new StringBuilder(s).reverse().toString();
	}

	/**获取京东验证码
	 * @param imgsrc
	 * @param rootPath
	 * @return
	 */
	public static String getCodeImg(String imgsrc,String rootPath) {
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response3 = null;
		String pathUrl = "";
		imgsrc = "http:"+imgsrc;
		try {
			 BasicCookieStore cookieStore = new BasicCookieStore();  
			 httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();  
			 // 3 获取验证码图片 并保存到指定路径  
	        RequestBuilder builder = RequestBuilder.get()  
	                .setUri(new URI(imgsrc + "&yys=" + new Date().getTime()));  
	        builder.setHeader("Accept", "image/webp,*/*;q=0.8");  
	        builder.setHeader("Accept-Encoding", "gzip, deflate, sdch");  
	        builder.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");  
	        builder.setHeader("Connection", "keep-alive");  
	        builder.setHeader("Host", "authcode.jd.com");  
	        builder.setHeader("Referer", "https://passport.jd.com/uc/login?ltype=logout");//必须。因为没弄这个浪费了很长时间  
	        builder.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");  
	        String url = "/upload/AuthCodeImg/"+System.currentTimeMillis()+".jpg";
	        String path = rootPath + url;
	        HttpUriRequest getAuthCode = builder.build();
	        response3 = httpclient.execute(getAuthCode); 
            HttpEntity entity = response3.getEntity();
            FileUtils.copyInputStreamToFile(entity.getContent(), new File(  
                    path));
            EntityUtils.consume(entity);
            pathUrl = url;
		} catch (Exception e) {
			_log.info("验证码获取图片失败。。。");
		}finally {
			if(httpclient != null){
				try {
					httpclient.close();
				} catch (IOException e) {
					_log.info("httpclient关闭失败");
				}
				if(response3 != null){
					try {
						response3.close();
					} catch (IOException e) {
						_log.info("CloseableHttpResponse关闭失败");
					}
				}
			}
        }  
		return pathUrl;
	}
	
}
