package com.raincc.robot.web.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.raincc.interceptor.JdLoginInterceptor;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.jd.core.jdutils.TUtils;

@ControllerBind(controllerKey="/zhuan")
public class zhuanlian extends AdminBaseController {

	private static Integer counter = 0;
	private static String _S = "";
	public static void main(String[] args) {
//		String s = "内购地址：3215，, 30内购地址：132 11215 30"+ 
//"全店p30";
//		Pattern p = Pattern.compile("([0-9]{6,})");
//		Matcher m = p.matcher(s);
//		m.find();
//		try {
//			System.out.println("aaaaaaaaa"+m.group());
//		} catch (Exception e) {
//			System.err.println("ccccc");
//		}
		
//		String string = "【狮王TOP 去渍洗衣液160ml】"+
//				"————————————————"+
//				"领券：http://jd.cn.hn/xcB"+
//				"备用券：http://t.cn/R9Jz7hc"+
//				"————————————————"+
//				"下单：https://union-click.jd.com/jdc?d=RVjDpG&skuId=354564544&sdfsdf"+
//				"【白领专享 · 京东保障】";
//		String a = stringNumbers(string.toString());
//		System.err.println(a);
//		String aString[] = string.split(a);
//		string = aString[1];
//		char[] c = string.toCharArray();                                                                                                                                                                                                                                                                                                                                                                
//		StringBuffer buffer = new StringBuffer();
//          for(int i = 0; i < c.length; i ++){
//             String len = Integer.toBinaryString(c[i]);
//              if(len.length() <= 8){
//            	 buffer.append(c[i]);
//              }
//          }
//          String aaString = "http";
//          System.err.println(aaString.lastIndexOf("t", 3));
//          System.err.println(buffer);
//           
//          System.err.println(buffer.indexOf("http://", 0));
		
		
		
		
//		String a = stringNumbers(string.toString());
//		System.err.println(a);
//		String aString[] = string.split(a);
//		for (String as : aString) {
//			System.err.println(">>>>"+as);
//		}
//		System.err.println(aString[2]);
//		int a = string.indexOf("skuId");
//		String b = string.substring(a);
//		int c = b.indexOf("&");
//		System.err.println(b.substring(0, c));
		
	}
	  public static String stringNumbers(String str)  
	    {  
	        if (str.indexOf("—")==-1)  
	        {  
	            return "";  
	        }  
	        else if(str.indexOf("—") != -1)  
	        {  
	            counter++;
	            if(counter % 2 == 0){
	            	_S += "—";
	            }
	            stringNumbers(str.substring(str.indexOf("—")+1));  
	            return _S;
	        }  
	        return "";  
	    }
	
//	@Before(JdLoginInterceptor.class)
	public void index(){
		String token = TUtils.getJdToken(this);
		_log.info("获取token为："+token);
		if(StringUtils.isBlank(token)){
			return;
		}
		
//		List<String>list = new ArrayList<String>();
//		list.add("10084176121");//方面面
//		list.add("12514354179");
//		list.add("11412208129");
//		String al = "10084176121,"+"12514354179,"+"0";
		/**
		 * {"jingdong_service_promotion_wxsq_getCodeBySubUnionId_responce":
		 * {"code":"0",
		 * "getcodebysubunionid_result":
		 * "{\"resultCode\":\"0\",\
		 * "resultMessage\":\"获取代码成功\",
		 * \"urlList\":{\"\\\"10084176121\\\"\":\"\",\"\\\"11412208129\\\"\":\"\",\"\\\"12514354179\\\"\":\"\"}}"}}
		 */
		
		
		net.sf.json.JSONObject netJoson = new net.sf.json.JSONObject();
//		netJoson.put("jdUnionId",unionId);
		netJoson.put("materialIds","12901353311");
		netJoson.put("proCont",1);
		netJoson.put("positionId",948043700);
		netJoson.put("subUnionId",2009829577);
		Long aLong2 = Long.parseLong(2009829577+"");
		String body = TUtils.getJdParams(aLong2,token, TConstants.JD_METHOD_getCodeBySubUnionId, netJoson);
		System.err.println(body);
		if(StringUtils.isNotBlank(body)){
			JSONObject jsonObject = JSONObject.fromObject(body);
			JSONObject j = jsonObject.getJSONObject("jingdong_service_promotion_wxsq_getCodeBySubUnionId_responce");
			JSONObject a = j.getJSONObject("getcodebysubunionid_result");
			String resultCode = a.getString("resultCode");
			String resultMessage = a.getString("resultMessage");
			_log.info("resultCode="+resultCode+",resultMessage="+resultMessage);
		}
		renderText(body);
//		JdClient client=new DefaultJdClient(SERVER_URL,accessToken,appKey,appSecret);
//		ServicePromotionWxsqGetCodeBySubUnionIdRequest request=new ServicePromotionWxsqGetCodeBySubUnionIdRequest();
//		request.setProCont( 123 ); 
//		request.setMaterialIds( "jingdong,yanfa,pop" ); 
//		request.setSubUnionId( "jingdong" ); 
//		request.setPositionId( 123 );
//		ServicePromotionWxsqGetCodeBySubUnionIdResponse response=client.execute(request);
	}
}
