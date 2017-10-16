package com.raincc.robot.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;

@SuppressWarnings("serial")
@TableBind(tableName="rb_wxusergroup",pkName="groupId")
public class WxUserGroup extends BaseModel<WxUserGroup> {

	public static final WxUserGroup dao = new WxUserGroup();

	
	/**
	 *  * [{"Uin":0,
	 * "UserName":"@@0482d7c59682842b5ebee329fb2f06a2a458d57c0da9329df29933ecd91d58af",
	 * "NickName":"测试",
	 * "HeadImgUrl":"/cgi-bin/mmwebwx-bin/webwxgetheadimg?seq=662570047&username=@@0482d7c59682842b5ebee329fbd37d",
	 * "ContactFlag":3,
	 * "MemberCount":0,
	 * "MemberList":[],
	 * "RemarkName":"",
	 * "HideInputBarFlag":0,
	 * "Sex":0,
	 * "Signature":"",
	 * "VerifyFlag":0,
	 * "OwnerUin":0,
	 * "PYInitial":"CS",
	 * "PYQuanPin":"ceshi",
	 * "RemarkPYInitial":"",
	 * "RemarkPYQuanPin":"",
	 * "StarFriend":0,
	 * "AppAccountFlag":0,
	 * "Statues":1,
	 * "AttrStatus":0,
	 * "Province":"",
	 * "City":"",
	 * "Alias":"",
	 * "SnsFlag":0,
	 * "UniFriend":0,
	 * "DisplayName":"",
	 * "ChatRoomId":0,
	 * "KeyWord":"",
	 * "EncryChatRoomId":"",
	 * "IsOwner":1}]
	 * 群信息处理
	 * @param jsonArray
	 * @param wechatMeta
	 * @return
	 */
	public JSONObject groupList(JSONArray jsonArray,
			final WechatMeta wechatMeta) {
		Integer Uin = wechatMeta.getUser().getIntValue("Uin");
		String base_uri = wechatMeta.getBase_uri();
		String cookies = wechatMeta.getCookie();
		JSONObject jox = new JSONObject();
		List<com.alibaba.fastjson.JSONObject> list = new ArrayList<com.alibaba.fastjson.JSONObject>();
		List<String> groupName = new ArrayList<>();
		for (int i = 0, len = jsonArray.size(); i < len; i++) {
			JSONObject jsonValue = jsonArray.getJSONObject(i);
			JSONObject jom = new JSONObject();
			System.out.println("jsonValue="+jsonValue);
			String UserName = jsonValue.getString("UserName") ;
			String NickName = jsonValue.getString("NickName") ;
			Integer groupUin = jsonValue.getIntValue("Uin");
			jom.put("UserName", UserName);
			jom.put("NickName",NickName);
			jom.put("Uin",groupUin);
			list.add(jom);
			String name = saveGroup(UserName,NickName,groupUin,Uin,base_uri,cookies);
			if(StringUtils.isNotBlank(name)){
				groupName.add(name);
			}
		}
		jox.put("Uin",Uin);
		jox.put("UserName",wechatMeta.getUser().getString("UserName"));
		jox.put("groupList", list);
		jox.put("groupName",JsonKit.toJson(groupName));
		return jox;
	}
	
	/**
	 * 保存用户群信息
	 * @param userName
	 * @param nickName
	 * @param groupUin
	 * @param uin
	 * @param base_uri
	 * @param cookies
	 */
	@Before(Tx.class)
	private synchronized String saveGroup(String userName, String nickName, Integer groupUin,
			Integer uin, String base_uri, String cookies) {
		WxUserGroup wx = new WxUserGroup().findFirst("select groupId from rb_wxusergroup where Uin = ? and NickName = ? ",uin,nickName);
		if(wx == null){
			WxUserGroup gu = WxUserGroup.dao.findFirst("select groupId from rb_wxusergroup where  NickName = ? ",nickName);
			if(gu != null){  //昵称有重复
				return nickName;
			}
			new WxUserGroup()
			.set("Uin", uin)
			.set("groupUin", groupUin)
			.set("toUserName", userName)
			.set("NickName", nickName)
			.set("base_uri",base_uri)
			.set("cookies",cookies)
			.set("createDate", new Date())
			.set("upTime", new Date())
			.set("isPutaway",true)
			.save();
		}else {
			wx.set("groupUin", groupUin);
			wx.set("toUserName", userName);
			wx.set("NickName", nickName);
			wx.set("base_uri",base_uri);
			wx.set("cookies",cookies);
			wx.set("upTime", new Date());
			wx.set("isPutaway",true);
			wx.update();
		}
		return null;
		
	}

	
}
