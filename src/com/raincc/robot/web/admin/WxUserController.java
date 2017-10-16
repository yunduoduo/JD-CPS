package com.raincc.robot.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.interceptor.WxLoginInterceptor;
import com.raincc.robot.entity.WxJdChannel;
import com.raincc.robot.entity.WxUser;
import com.raincc.robot.entity.WxUserGroup;

/**用户信息
 * @author Administrator
 *
 */
@ControllerBind(controllerKey="/robotAdmin/rob/user",viewPath="admin/robot/user")
@Before({AdminInterceptor.class,WxLoginInterceptor.class})
public class WxUserController extends AdminBaseController {
	
	
	/**
	 * 设置群发群
	 */
	public void sendMsg(){
		String ids = getPara("ids");
		String[] id = ids.split("&");
		Integer uin = getParaToInt("uin");
		Db.update("update rb_wxusergroup set isValid = 0 where Uin = ? and isValid = 2 ",uin);
		if(StringUtils.isBlank(ids)){
			renderTemplateJson("0","已经取消所有群发群");
			return;
		}
		for (String groupId : id) {
			Integer gd = Integer.parseInt(groupId);
			Db.update("update rb_wxusergroup set isValid = 2 where groupId = ? ",gd);
		}
		renderTemplateJson("0","设置成功。。。");
	}
	//微信登录用户列表
	public void wxUserList(){
		Integer pageNo = getParaToInt(0,1);
		Integer adminId = getAdminId();
		Page<WxUser> page = WxUser.dao.paginate(pageNo, 20, "select *  ", 
				" from  rb_wxuser where adminId = ? and isValid = true ",adminId);
		for (WxUser wx : page.getList()) {
			String aString = wx.getStr("userNickName");
			aString = aString.replace("<", "&lt;").replace(">", "&gt;");
			wx.put("userNickName", aString );
		}
		setAttr("page", page);
	}
	
	/**
	 * 设置群推广位
	 */
	public void setChannel(){
		String groId = getPara("groId");
		String channelId = getPara("channelId");
		if(StringUtils.isNotBlank(channelId) && "0".equals(channelId.trim())){
			Db.update("update rb_wxusergroup set channelId = ? where groupId = ? ",channelId,groId);
			renderTemplateJson("0","您取消了推广位");
			return;
		}else {
			Db.update("update rb_wxusergroup set channelId = ? where groupId = ? ",channelId,groId);
		}
		renderTemplateJson("0","设置成功");
		
	}
	
	/**
	 * 群发群
	 */
	public void sendList(){
		Integer uin = getParaToInt("uin",0);
		Integer pageNo = getParaToInt(0,1);
		Page<WxUserGroup> page = WxUserGroup.dao.paginate(pageNo, 20, "select ch.spaceName,g.groupId,g.NickName,u.userNickName,g.isValid ", 
				" from rb_wxusergroup g "
				+ " left join rb_wxuser u on g.Uin = u.Uin "
				+ " left join rb_jdchannel ch on ch.channelId = g.channelId "
				+ " where g.Uin = ? and g.isPutaway = true order by g.groupId desc ,g.upTime desc ",uin);
		for (WxUserGroup wx : page.getList()) {
			String aString = wx.getStr("userNickName");
			String bString = wx.getStr("NickName");
			bString = bString.replace("<", "&lt;").replace(">", "&gt;");
			aString = aString.replace("<", "&lt;").replace(">", "&gt;");
			wx.put("uname", aString );
			wx.put("gname", bString );
		}
		setAttr("page",page);
		setAttr("uin", uin);
		Long unionId = getJDUnionId();
		if(unionId == null){
			setAttr("channel",new ArrayList<>());
		}else {
			List<WxJdChannel> channel = WxJdChannel.dao.find("select channelId,jdChannelId,spaceName from rb_jdchannel where unionId = ? ",unionId);
			setAttr("channel",channel);
		}
	}
	
	
	
	/**
	 * 用户群列表
	 */
	public void groupList(){
		Integer uin = getParaToInt("uin",0);
		Integer pageNo = getParaToInt(0,1);
		Page<WxUserGroup> page = WxUserGroup.dao.paginate(pageNo, 20, "select ch.spaceName,g.groupId,g.NickName,u.userNickName,g.isValid ", 
				" from rb_wxusergroup g "
				+ " left join rb_wxuser u on g.Uin = u.Uin "
				+ " left join rb_jdchannel ch on ch.channelId = g.channelId "
				+ " where g.Uin = ? and g.isPutaway = true order by g.groupId desc ,g.upTime desc ",uin);
		for (WxUserGroup wx : page.getList()) {
			String aString = wx.getStr("userNickName");
			String NickName = wx.getStr("NickName");
			NickName = NickName.replace("<", "&lt;").replace(">", "&gt;");
			aString = aString.replace("<", "&lt;").replace(">", "&gt;");
			wx.put("uname", aString );
			wx.put("NickName", NickName );
		}
		setAttr("page",page);
		setAttr("uin", uin);
		
	}
}
