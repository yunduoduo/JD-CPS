package com.raincc.web;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.raincc.common.RcConstants;
import com.raincc.model.Admin;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.JdPermission;
import com.raincc.robot.entity.WxJdUser;

public abstract class BaseController extends Controller {
	
	protected static final Logger _log = Logger.getLogger(BaseController.class);
	
	public Integer getAdminId(){
		Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_admin);
		if(admin != null){
			return admin.getInt("adminId");
		}
		return null;
	}
	
	public  String getJdToken() {
		String token = getCookie(TConstants.JD_ACCESS_TOKEN);
		if(StringUtils.isBlank(token)){
			JdPermission jp = JdPermission.dao.findFirst("select JD_AccessToken from rb_jdpermission where unionId = ? and tokenValidTime > UNIX_TIMESTAMP() ",TConstants.BL_JD_UNIONID);
			if(jp != null){
				if(StringUtils.isNotBlank(jp.getStr("JD_AccessToken"))){
					setCookie(TConstants.JD_ACCESS_TOKEN,jp.getStr("JD_AccessToken"),1 * 24 * 60 * 60);
					return jp.getStr("JD_AccessToken");
				}
			}
		}else {
			return token;
		}
		return null;
	}
	
	public Admin getAdmin(){
		Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_admin);
		return admin;
	}
	
	public Integer getAdminCliId(){
		Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_adminCli);
		if(admin != null){
			return admin.getInt("adminId");
		}
		return null;
	}
	
	public  Integer getJDUserId(){
		Integer userId = (Integer) getSession().getAttribute(TConstants.JD_USERID);
		return userId;
	}
	
	public static String getBLJDCookies(){
		return WxJdUser.dao.findFirst("select cookie from rb_wxjduser where unionId = ? ",TConstants.BL_JD_UNIONID).getStr("cookie");
	}
	public WxJdUser getJDUser(){
		Integer userId = (Integer) getSession().getAttribute(TConstants.JD_USERID);
		return new WxJdUser().findById(userId);
	}
	public Long getJDUnionId(){
		Long uniLong = null;
		Integer userId = (Integer) getSession().getAttribute(TConstants.JD_USERID);
		if(userId != null && userId > 0){
			uniLong = new WxJdUser().findFirst("select unionId from rb_wxjduser where accountsId = ? ",userId).getLong("unionId");
			if(uniLong != null && uniLong > 0){
				getSession().setAttribute(TConstants.JD_UnionId,uniLong);
			}
		}
		
		return uniLong;
	}
	public boolean getBoJDChannel(){
		return TConstants.BL_JD_UNIONID.longValue() == getJDUnionId().longValue();
	}
	public static String getTAccessToken(){
		return JdPermission.dao.findFirst("select JD_AccessToken from rb_jdpermission where unionId = ? ",TConstants.BL_JD_UNIONID).getStr("JD_AccessToken");
	}
	protected enum PageTip {
		success, danger
	}
	
	/**获取客户端临时京东主键
	 * @return
	 */
	public Long getCliJDtemporaryId(){
		Long union = (long) 0;
		Admin adminCli = (Admin) getSession().getAttribute(RcConstants.session_login_adminCli);
		Admin admin = Admin.dao.findById(adminCli.getInt("adminId"));//最新状态session中可能不是最新
		if(admin != null && admin.getInt("JDtemporaryId") > 0){
			union = WxJdUser.dao.findFirst("select unionId from rb_wxjduser where accountsId = ? ",admin.getInt("JDtemporaryId")).getLong("unionId");
		}
		return union;
	}
	
	/**怕不断是否是个人用户
	 * @return
	 */
	public Boolean getIndividual(){
		Boolean bo = false;
		Admin admin = (Admin) getSession().getAttribute(RcConstants.session_login_admin);
		if(admin != null){
			return admin.getInt("roleId") == 2;
		}
		return bo ;
	}
	protected void setPageTip(String pageTip) {
		getSession().setAttribute("pageTip", pageTip);
		getSession().setAttribute("pageTipState", PageTip.success);
	}
	
	protected void setPageTip(String pageTip, String pageTipState) {
		getSession().setAttribute("pageTip", pageTip);
		getSession().setAttribute("pageTipState", pageTipState);
	}
	
	protected void renderTemplateJson(String code, String info) {
		JSONObject jo = new JSONObject();
		jo.put("code", code);
		jo.put("info", info);
		renderJson(jo);
	}
	
	protected JSONObject getTemplateJson(String code, String info) {
		JSONObject jo = new JSONObject();
		jo.put("code", code);
		jo.put("info", info);
		return jo;
	}
	
	protected String getReqMethod() {
		return getRequest().getMethod();
	}
	
	public void renderJson(Object obj) {
		_log.info("renderJson:" + obj);
		super.renderJson(obj);
	}
	
	public Date getParaToDateTime(String para) {
		String value = getPara(para);
		if (value == null || "".equals(value.trim()))
			return null;
		try {
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Date getParaToDateByPattern(String para, String pattern) {
		String value = getPara(para);
		if (value == null || "".equals(value.trim()))
			return null;
		try {
			return new java.text.SimpleDateFormat(pattern).parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String unicode) {
	 
	    StringBuffer string = new StringBuffer();
	 
	    String[] hex = unicode.split("\\\\u");
	 
	    for (int i = 1; i < hex.length; i++) {
	 
	        // 转换出每一个代码点
	        int data = Integer.parseInt(hex[i], 16);
	 
	        // 追加成string
	        string.append((char) data);
	    }
	 
	    return string.toString();
	}
	/**
	 * 字符串转换unicode
	 */
	public static String string2Unicode(String string) {
	 
	    StringBuffer unicode = new StringBuffer();
	 
	    for (int i = 0; i < string.length(); i++) {
	 
	        // 取出每一个字符
	        char c = string.charAt(i);
	 
	        // 转换为unicode
	        unicode.append("\\u" + Integer.toHexString(c));
	    }
	 
	    return unicode.toString();
	}

}
