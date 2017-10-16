package com.raincc.robot.entity;
import static com.github.kevinsawicki.http.HttpRequest.METHOD_GET;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.kevinsawicki.http.HttpRequest;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.jd.core.jdutils.HttpUtils;
import com.raincc.robot.jd.core.jdutils.TUtils;
import com.raincc.robot.util.HttpRequestUtils;

@SuppressWarnings("serial")
@TableBind(tableName="rb_wxjduser",pkName="accountsId")
public class WxJdUser extends BaseModel<WxJdUser> {

	public static final WxJdUser dao = new WxJdUser();

	@Before(Tx.class)
	public Integer saveUser(String loginname, String password, String cookieValue,Integer adminId,Boolean bo) {
		WxJdUser wx = WxJdUser.dao.findFirst("select accountsId from rb_wxjduser where loginname = ? ",loginname);
		Integer id = 0;
		if(wx == null ){
			//获取联盟ID
			WxJdUser wxJdUser = new WxJdUser();
			wxJdUser.set("loginname",loginname);
			wxJdUser.set("password",password);
			wxJdUser.set("cookie",cookieValue);
			wxJdUser.set("createDate",new Date());
			wxJdUser.set("upTime",new Date());
			wxJdUser.set("adminId",adminId);
			wxJdUser.save();
			id = wxJdUser.getInt("accountsId");
		}else {
			wx.set("adminId",adminId);
			wx.set("loginname",loginname);
			wx.set("password",password);
			wx.set("cookie",cookieValue);
			wx.set("createDate",new Date());
			wx.set("upTime",new Date());
			wx.update();
			id = wx.getInt("accountsId");
		}
		if(id != null && id > 0){
			updateJXJunion(id,cookieValue,bo);
			Db.update("update rc_admin set JDtemporaryId = ? where adminId = ? ",id,adminId);
		}
		return id;
	}

	/**
	 * 获取联盟号和京享街号
	 * @param id
	 * @param cookieValue
	 * @param bo
	 */
	private void updateJXJunion(Integer id, String cookieValue,Boolean bo) {
		HttpRequest getLoginPageReq=HttpUtils.request(TConstants.JD_JXJUNION_URL, METHOD_GET);//请求登录页面
		getLoginPageReq.header("Cookie",cookieValue);
		String body=getLoginPageReq.body();
		Document doc=Jsoup.parse(body);
		Elements scripts=doc.select("script");
		for(Element script:scripts){
			String text=script.html();
			if(text.contains("new DetailReport")){//先定位到是哪一个script脚本
				_log.info("获取京享街号内的js:"+text);
				int a = text.indexOf("(");
				int b = text.indexOf(")");
				String unsString = text.substring(a+1,b);
				String c = unsString.replace(" ","");
				String[] unions = c.split(",");
				if(unions.length == 2){
					String jdUnion = unions[0];
					String jxjUnion = unions[1];
					Long jLong = Long.parseLong(jxjUnion.trim());
					Long jdLong = Long.parseLong(jdUnion.trim());
					Db.update("update rb_wxjduser set unionId = ?, JXJunionId = ? where accountsId = ? ",jdLong,jLong,id);
					if(bo){
						for (String stringL : unions) {
							Long unioLong = Long.parseLong(stringL.trim());
							if(unioLong != null && unioLong > 0){
								//更新当天订单
								updateDayOrder(cookieValue,unioLong);
							}
						}
					}
				}
			}
		}
	}
	
	/**个人用户更新当天订单
	 * @param cookie
	 */
	private void updateDayOrder(final String cookie,final Long unioLong) {
		 new Thread(new Runnable() {
			@Override
			public void run() {
					Integer count = 1;
					Integer conData = 0;
					while (true) {
						try {
							net.sf.json.JSONObject  data = new net.sf.json.JSONObject();
							net.sf.json.JSONObject j1 = new net.sf.json.JSONObject();
							j1.put("total",0);
							j1.put("pageNum",count);
							j1.put("size",20);
							data.put("pagination", j1);
							data.put("order","[]");
							data.put("data", "[]");
							List<String>list = new ArrayList<String>();
							net.sf.json.JSONObject j2 = new net.sf.json.JSONObject();
							j2.put("name","orderStatus");//查询是否有效，有效value是1，全部值为空
							j2.put("value","");
							list.add(j2.toString());
							net.sf.json.JSONObject j3 = new net.sf.json.JSONObject();
							j3.put("name","accountDateStr");
							String dateString = TUtils.getDateYMdHms("yyyy-MM", new Date());
							String dateString2 = TUtils.getDateYMdHms("yyyy-MM-dd", new Date());
//							j3.put("value",dateString+"-01"+"#"+dateString2);
							j3.put("value",dateString2+"#"+dateString2);
							list.add(j3.toString());
							net.sf.json.JSONObject shortcutDate = new net.sf.json.JSONObject();
							shortcutDate.put("name","shortcutDate");//快捷时间，今天value=0，昨天是1，过去7天就是7
							shortcutDate.put("value","");
							list.add(shortcutDate.toString());
//							JSONObject orderJo = new JSONObject();
//							orderJo.put("name","orderId");
//							orderJo.put("value","");
//							list.add(orderJo.toString());
							
							net.sf.json.JSONObject uniJo = new net.sf.json.JSONObject();
							uniJo.put("name","unionId");
							uniJo.put("value",unioLong);
							list.add(uniJo.toString());
							
							data.put("search", list);
							String body = HttpRequestUtils.sendHttpUrl(TConstants.JD_PY_ImportOrders, data.toString(),cookie,TConstants.ContentType_PAYLOAD);
							if(StringUtils.isNotBlank(body) && !body.contains("<body")){
								net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(body);
								net.sf.json.JSONArray dataArray = jsonObject.getJSONArray("data");
								_log.info("结果有"+dataArray.size()+"数据。");
								conData += dataArray.size();
								if(dataArray.size() > 0){
									for (int i = 0; i < dataArray.size(); i++) {
										net.sf.json.JSONObject jo = dataArray.getJSONObject(i);
										new JdImportOrders().individual(jo);
									}
									count ++;
									Thread.sleep(1500);
								}else {
									_log.info("一共执行了"+conData+"数据。");
									return;
								}
							}else {
								_log.info("一共执行了"+conData+"数据。");
								return;
							}
						} catch (Exception e) {
							_log.info("一共执行了"+conData+"数据。抛出异常："+e);
							return;
						}
						
					}
			}
			}).start();
	}

}
