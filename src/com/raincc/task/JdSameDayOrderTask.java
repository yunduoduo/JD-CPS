package com.raincc.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.jfinal.log.Logger;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.JdImportOrders;
import com.raincc.robot.entity.JdPermission;
import com.raincc.robot.entity.WxJdUser;
import com.raincc.robot.jd.core.jdutils.TUtils;
import com.raincc.robot.util.HttpRequestUtils;
public class JdSameDayOrderTask implements Job {
	
	private static final Logger _log = Logger.getLogger(JdSameDayOrderTask.class);
	
	/* (non-Javadoc)
	 * 当天订单更新
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		_log.info("JdSameDayOrderTask:每20分钟更新一次订单");
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		if(minute % 20 == 0){
			impOrder();
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				_log.info("JdSameDayOrderTask-sleep异常");
				e.printStackTrace();
			}
			crawling();
		}
		
	}
	/**
	 * 晚一分钟更新一次京东接口
	 */
	private void crawling() {
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
				String dateString = TUtils.getDateYMdHms("yyyy-MM-dd", new Date());
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
//				com.alibaba.fastjson.JSONObject bodyJson = HttpRequestUtils.httpRequest(TConstants.JD_PY_ImportOrders, "POST", data.toString(), cookie, TConstants.ContentType_PAYLOAD);
//				String body = bodyJson.toJSONString();
				if(StringUtils.isNotBlank(body) && !body.contains("<body")){
					JSONObject jsonObject = JSONObject.fromObject(body);
					JSONArray dataArray = jsonObject.getJSONArray("data");
					_log.info("结果有"+dataArray.size()+"数据。");
					conData += dataArray.size();
					if(dataArray.size() > 0){
						for (int i = 0; i < dataArray.size(); i++) {
							JSONObject jo = dataArray.getJSONObject(i);
							synchronized (JdSameDayOrderTask.class) {
								new JdImportOrders().crawling(jo);
							}
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
	/**
	 * 15更新一次京东api接口
	 */
	private void impOrder() {
		JSONObject netJoson = new JSONObject();
		Long unionId = TConstants.BL_JD_UNIONID;
		String token = JdPermission.dao.findFirst("select JD_AccessToken from rb_jdpermission where unionId = ? ",unionId).getStr("JD_AccessToken");
		netJoson.put("unionId",unionId);
		netJoson.put("time",TUtils.getDateYMdHms("yyyyMMdd",new Date()));
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
				synchronized (JdSameDayOrderTask.class) {
					new JdImportOrders().saveOrder(j,unionId);
				}
			}
		}
		
	}

}
