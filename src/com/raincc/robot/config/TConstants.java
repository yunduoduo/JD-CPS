package com.raincc.robot.config;

import java.text.SimpleDateFormat;

public class TConstants {
	
	public static final String session_weixin_openid = "__robot_weixin_openid";
	public static final String session_login_userId = "__robot_login_userId";
	public static final String session_login_user = "__robot_login_user";
	public static final String session_login_str = "__robot_login_str";
	public static 		String PJECT_URL;
//	客户端京东账户主键记录
	public static 		String session_JDtemporaryId = "_JDtemporaryId";
	public static final String WX_UIN = "WX_UIN";
	
	
	public static 		String JD_BACKURL;
	//授权必须传当前url
	public static 		String JD_STRINGBACKURL = "JD_BACKURL";
//	京东用户ID
	public static final String JD_USERID = "_JD_USERID";
	
	public static final String JD_UnionId = "JD_UnionId";
	
	
	public static 		String JD_Accredit_callbackURL = "http://dev.meyoutao.com:8080/robot/accreditBack";
//  京东授权返回url(bailing)
//	public static 		String JD_Accredit_callbackURL = "http://blc.bailingclub.cn/robot/accreditBack";
	
	
//	京东accessToken
	public static final String JD_ACCESS_TOKEN = "Jd_ACCESS_TOKEN";
//	京东刷新授权地址
	public static final String JD_REFRESH_TOKEN_Url = "https://oauth.jd.com/oauth/token?client_id={client_id}&client_secret={client_secret}&grant_type=refresh_token&refresh_token={refresh_token}";
//	京东登录
	public static final String JD_LOGINNAME = "JD_LoginName";
	public static final String JD_LOGINPWD = "JD_LOGINPWD";
	
//	主京东联盟ID
	public static		Long   BL_JD_UNIONID;
	public static 		String T_LOGINNAME;
	public static 		String T_PASSWORD;
	public static 		String T_JDLOGIN_EID;
	public static 		String T_LDLOGIN_FP;
//					  YRQJ6VQPYVTJ4IW4NOFUQVH7NVJNYWJWNB43A3A7GZWHNHX4N6CBNI5QPTOQFMTLVJHKUFJVYAKBYGLVHCIYKXGBPI
//					  YRQJ6VQPYVTJ4IW4NOFUQVH7NVJNYWJWNB43A3A7GZWHNHX4N6CBNI5QPTOQFMTLVJHKUFJVYAKBYGLVHCIYKXGBPI
//	3AB9D23F7A4B3C9B:"YRQJ6VQPYVTJ4IW4NOFUQVH7NVJNYWJWNB43A3A7GZWHNHX4N6CBNI5QPTOQFMTLVJHKUFJVYAKBYGLVHCIYKXGBPI"


//	PTPDS4E7KT2EEON3R7MNYTDUVOQTDLNLKVT5PBCMLNROXI2ZKYFWBMUCBARTJGYLNKJSDVBHDGFBWYMHIBZTM3LYRU
//	https://payrisk.jd.com/fcf.html?g=7TJI7TceJhZPW4NdFgEj7Tce7TZ37TceZLZPwB4kOg6eZTiBZlfQFTPGZHbQigAjZBFHZH6kwlAPwHcPw4wPwH7XWQPdygDPwHcPwj<PwH7*ieAewGAe6eAewQxbzQJkigJP7Tce7TZ37TceqQaC6jDPwHcPw4wPwH7XWeAewGAB6SAewdJpzQRXJtwPwHcPw4wPwH7XWkFPWdZpzlDPwHcPwj<PwHcE7Tce7T7L7Tceid7XJtZPWGAewGAB6SAew4FpWQfQztaPwHcPw4wPwH7GWQ9tWlfefQfeWlPXzGAewGAB6SAewHAevH2PwHcPw4wPwH7HzlxXW4RPW1Ro7Tce7TZ3wH6Pw4wPwH7Bit7PFgESFhZXz1fjyg9u7Tce7TZ37TceZBiDqL<BZHiPwHcPw4wPwH7jygkPqQ9uFA9QFdZPJIAewGAB6SjD7T7L7TceWlfBWlPXzPZjzt7bFlAPwHcPwjNjWdfP7T7L7Tcez09HigxTJ09eigJP7Tce7TZ3J17kFSAe6eAewQPuF0fDFgR<iGAewGAB6hReJgAPw4wPwH7bF0RIFgbbJQPXWGAewGAB6gFbz1ZP7T7L7Tcezt3Pz4RbJ0NGihZP7Tce7TZ3FQNsWlAPw4wPwH7HW1APwHcPwj<PwH7kzQCuztJu7Tce7T7L7TceW0xbJ0FXWQjPwHcPwj<PwH7hygDBwGAewGAe6eAewdReigZr7Tce7TZ37TceJgEBW0fHygFpFg6PwHcPw4wPwH7*z1fdygEB7Tce7TZ37TceJgE4FgFpzQf47Tce7T7L7TceilNuJQNB7Tce7TZ37TceigADil6kZHJ4OL7HiBRQiTieZH2jOL2*FgNQZT7QiBAPwHcPw4wPwH7tFg7dz<F*7Tce7TZ37TcewQ7biT7HZgFPZB2EZBceFTRbZlwtZQZHZT4*iQ6BwQcPwHcPw4wPwH7QygEdFh7*WQPuJIAewGAB6SAewQiDwHW*ZQwjZBWBiTijZTPbiH74ZQNbFQfPOLNHFHZH7Tce7T7L7TceFgP47Tce7TZ37Tcegf7RSHFgAf3FfPRKZ<PhZ<E5RPfRf4atTPFKTPPhSPJO6H6B6TZ3ZjJyfjbOSNajTHFL64E7ZfN6f<9RR4kATNFKS<CfR4pggANv6PP1TNFc6jPFSkb16P377Tce7TJ</&a=7TJI7TceW0Pu7Tce7TZ37Tce7Tce7T7L7TcezlP47Tce7TZ37Tce7Tce7T7L7TceWIAewGAB6SAewdwPwHcPw4wPwH7QWIAewGAB6SAewQiDwHW*ZQwjZBWBiTijZTPbiH74ZQNbFQfPOLNHFHZH7Tce7T7L7TcezeAewGAB6SAewd3bWtZ*zt7jvQp4vQZXzSAeRQEPJeAeRQxXFlPuvQNBW1aPwHcPw4wPwH7QieAewGAB6SAewPPSAAolfPN6gfFASHR7fBROTjFfAfFcZjEgS4EFfjphT4cjwj<B6TJ1gPJcT4biZ<Dl6j7OSTfRANR5AAFZf<xgS4bvfAFKfPP3Sj7FRjxgS<Z7gACiRj76SSAewGAe6eAewd6PwHcPwj<PwH7cTANIZAp0TPf<SNPg6BFNZ4C76BFifBRTZfZwfkbiZBR3T<95RNP6SkbvSTWkR<PZfLFvAkZ<ZH7ORfckg<PSZ4p3SNZIfAJNRkJFTeAewGAtR2/
	
	public static final String JD_LoginPage="https://passport.jd.com/new/login.aspx";
	public static final String JD_LoginUrl="https://passport.jd.com/uc/loginService";
	public static final String JD_LoginSuccess="({\"success\":\"//www.jd.com\"})";
	public static final String JD_SESSIONID="thor";//京东登录后的会话ID
	public static 		String JD_Appkey; //京东授权appid,即client_id
	
	public static 		String JD_AppSecret;//京东授权AppSecret,即client_secret
	
//	获取渠道 
	public static final String  JD_CHANNEL = "https://media.jd.com/gotoadv/selectProMediaType";
	
//	获取渠道中的商品 
	public static final String  JD_CHANNEL_PRODUCT = "https://media.jd.com/gotoadv/cpsPromotionCenter";
	public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final String format = "json";
	
	public static final String v = "2.0";
//	京东接口地址
	public static final String JD_URL = "https://api.jd.com/routerjson";
	
//  联盟微信手q通过subUnionId获取推广链接
	public static final String JD_METHOD_getCodeBySubUnionId = "jingdong.service.promotion.wxsq.getCodeBySubUnionId";
	
//	获取推广商品信息接口
	public static final String JD_METHOD_getGoodsInfo = "jingdong.service.promotion.goodsInfo";      
	
//	查询引入订单
	public static final String JD_METHOD_getQueryImportOrders = "jingdong.UnionService.queryImportOrders";      
	
//	爬取京东引入订单地址
	public static final String JD_PY_ImportOrders = "https://media.jd.com/rest/report/detail/in/page";
//	个人用户获取京享街union地址
	public static final String JD_JXJUNION_URL = "https://media.jd.com/rest/report/detail";
	
	public static final String ContentType_PAYLOAD = "application/json; charset=UTF-8";
	
	public static final String ContentType_FORMDATA = "application/x-www-form-urlencoded; charset=utf-8";
//	查询京东验证码状态地址
	public static final String JD_showAuthCode = "https://passport.jd.com/uc/showAuthCode";
	/**
	 * 手机验证码
	 */
	public static final String SESSION_MOBILE_CAPTCHA = "session_mobile_captcha";
	
	public static String website;
	
//	public static String wx_AppId;
//	public static String wx_AppSecret;
//	public static String wx_token;
//	public static String wx_oriId;
	
	public static String wsq_AppID;
	public static String wsq_AppSecret;
	public static String wsq_sId;
	
	public static String baiduKey;
	
	public static String qqLBSKey;
	
	public static String SESSION_USER_CURRENT_CITYID = "session_user_current_cityId";

	

}
