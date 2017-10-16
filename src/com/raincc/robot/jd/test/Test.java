package com.raincc.robot.jd.test;

import static com.github.kevinsawicki.http.HttpRequest.METHOD_POST;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import com.raincc.robot.entity.WxJdChannel;
import com.raincc.robot.jd.core.jdutils.HttpUtils;

public class Test {

	/**
	 * {"promotionSite":
	 * [{"id":944526213,
	 * "unionId":1000165119,
	 * "siteId":0,
	 * "spaceName":"聊天推广位测试",
	 * "status":1,
	 * "createTime":1501142958000,
	 * "updateTime":1501464322000,
	 * "siteName":null,
	 * "update":0,
	 * "placementId":null,
	 * "promotionType":0,
	 * "placeUpTime":null,
	 * "type":0,
	 * "clickCount":null, "orderCount":null,"orderPrice":null,"orderCommission":null,
	 * "unionType":0,
	 * "ruleId":null,"ruleName":null,"ruleListId":null,"platform":null,
	 * "isDefault":0}]}
	 */
	
//	public static final String  url = "https://media.jd.com/gotoadv/goods?pageSize=50";
	
//	获取渠道 
//	public static final String  url = "https://media.jd.com/gotoadv/selectProMediaType";
	
	//获取渠道中的商品 
	public static final String  url = "https://media.jd.com/gotoadv/cpsPromotionCenter";
	
	//方面面： http://union-click.jd.com/jdc?e=0&p=AyIHZRprFQMTDlAbXRICEQdlXwRNXE03DF4eVAkMGQ1eD0kdSVJKSQVJHBIGVBJeFQQVB1YbRExHTlplfF0UaW5PFGI5UhhRYxJvJkscQmczXVcZMhM3VRpaFAIVA1UbUyUyEgdlWjUVARYBVB9rHQcXA1YcWBUAIgdRGlsRAxYAVB1eHQIiBVUrWxcLEgZWH1MRAxdpFCtrJTI%3D&t=W1dCFBBFC1pXUwkEAEAdQFkJBVsUAxsCVR1cFQESGAxeB0g%3D
//			 http://union-click.jd.com/jdc?e=0&p=AyIHZRprFQMTDlAbXRICEQdlXwRNXE03DF4eVAkMGQ1eD0kdSVJKSQVJHBIGVBJeFQQVB1YbRExHTlplfF0UaW5PFGI5UhhRYxJvJkscQmczXVcZMhM3VRpaFAIVA1UbUyUyEgdlWjUVARYBVB9rHQcXA1YcWBUAIgdRGlsRAxYAVB1eHQIiBVUrWxcLEgZWH1MRAxdpFCtrJTI%3D&t=W1dCFBBFC1pXUwkEAEAdQFkJBVsUAxsCVR1cFQESGAxeB0g%3D
	public static void main(String[] args) {
		
		//////////////////获取渠道//////////////////////
//		String cookie = "ceshi3.com=103; _pst=13810259504_p; logining=1; _tp=EydV9%2BRui31qSe9kFI2Xtw%3D%3D; ol=1; thor=F5EB9B92BC3BDCA88186235D6041FFEE7239FB5EDB5BB405BBE11D2A3FD15E56473FC0816291934CC4C2D1AF1964B472492415715290F8C73E4CF750144FB55BF690900A1214B3F16AEE681967AE99F40C4BCABAD03C8249A7474AC29162D1D9B831C70AAC04953DF192BCBDE4EC2701828F10D2B9EBF7469313CE6E4C5AACF19DD0A9031C74599AA89F83F15FE0B5B5; unick=huangwenke0703; pin=13810259504_p; pinId=_k4z3IclNH0YbGcn3n34qw; TrackID=1GR3yc8_fyNnV_0nbcFTUt_RfuJbOgj1D1SEisZYbpKm951CBvLko0p0eF-54zJKaOVncQ8rbHf5-bA_jzTzNwYthybtTq5WIIoZW144b65U; mp=13810259504; sc_t=1; _ntBWNVf=''; login_c=1";
//		HttpRequest getOrderPageReq=HttpUtils.request(url, METHOD_POST);//请求订单页面
//		Map<String,Object> data = new HashMap<String,Object>();
//		data.put("Content-Type","application/x-www-form-urlencoded" );
//		data.put("id",0);
//		data.put("type", 4);
//		data.put("status", 1);
//		getOrderPageReq.header("Cookie", cookie).form(data);
//		String body=getOrderPageReq.body();
//		System.err.println("===========>"+body);
//		JSONObject jObject = JSONObject.parseObject(body);
//		JSONArray jsonArray = jObject.getJSONArray("promotionSite");
//		for (int i = 0; i < jsonArray.size(); i++) {
//			JSONObject jo = jsonArray.getJSONObject(i);
//			WxJdChannel.dao.saveChannel(jo);
//		}
		/////////////////////获取渠道中的商品//////////////////////////////
		String cookie = "ceshi3.com=103; _pst=13810259504_p; logining=1; _tp=EydV9%2BRui31qSe9kFI2Xtw%3D%3D; ol=1; thor=F5EB9B92BC3BDCA88186235D6041FFEE7239FB5EDB5BB405BBE11D2A3FD15E56473FC0816291934CC4C2D1AF1964B472492415715290F8C73E4CF750144FB55BF690900A1214B3F16AEE681967AE99F40C4BCABAD03C8249A7474AC29162D1D9B831C70AAC04953DF192BCBDE4EC2701828F10D2B9EBF7469313CE6E4C5AACF19DD0A9031C74599AA89F83F15FE0B5B5; unick=huangwenke0703; pin=13810259504_p; pinId=_k4z3IclNH0YbGcn3n34qw; TrackID=1GR3yc8_fyNnV_0nbcFTUt_RfuJbOgj1D1SEisZYbpKm951CBvLko0p0eF-54zJKaOVncQ8rbHf5-bA_jzTzNwYthybtTq5WIIoZW144b65U; mp=13810259504; sc_t=1; _ntBWNVf=''; login_c=1";
		HttpRequest getOrderPageReq=HttpUtils.request(url, METHOD_POST);//请求订单页面
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("Content-Type","application/x-www-form-urlencoded" );
		data.put("pageIndex","");
		data.put("pageSize",10);
		data.put("startTime","2017-05-03");
		data.put("endTime", "");
		data.put("promotionType", -1);//推广方式id，全部
		data.put("promotionName", "京东联盟app默认推广位");//推广名称
		getOrderPageReq.header("Cookie", cookie).form(data);
		String body=getOrderPageReq.body();
		
		System.err.println("===========>"+body);
		Document document = Jsoup.parse(body);
		Elements elements = document.select(".background_fff_k .person");
		System.err.println(elements.toString());
	
	}
}
