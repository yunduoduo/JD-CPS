package com.raincc.robot.entity;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;


@SuppressWarnings("serial")
@TableBind(tableName="rb_wxuser",pkName="wxUserId")
public class WxUser extends BaseModel<WxUser> {

	public static final WxUser dao = new WxUser();

	/**
	 * 更新用户
	 * @param wechatMeta
	 */
	@Before(Tx.class)
	public void upUser(WechatMeta wechatMeta) {
		Db.update("update rb_wxuser set isValid = true,userNickName = ? ,UserName = ?,HeadImgUrl = ?,upTime= ?  where Uin = ?",wechatMeta.getUser().getString("NickName")
				,wechatMeta.getUser().getString("UserName"),wechatMeta.getUser().getString("HeadImgUrl"),new Date(),wechatMeta.getUser().getIntValue("Uin"));
		WxWechatMeta wx = new WxWechatMeta().findFirst("select * from rb_wxwechatmeta where Uin = ? ",wechatMeta.getUser().getIntValue("Uin"));
		boolean bo = false;
		if(wx == null){
			wx = new WxWechatMeta();
			bo = true;
		}
		saveOrUpWx(bo,wx,wechatMeta);
	}
	
	
	@Before(Tx.class)
	private void saveOrUpWx(boolean bo, WxWechatMeta wx,WechatMeta wechatMeta) {
		wx.set("Uin",wechatMeta.getUser().getIntValue("Uin"));
		wx.set("base_uri",wechatMeta.getBase_uri());
		wx.set("redirect_uri",wechatMeta.getRedirect_uri());
		wx.set("webpush_url",wechatMeta.getWebpush_url());
		wx.set("uuid",wechatMeta.getUuid());
		wx.set("skey",wechatMeta.getSkey());
		wx.set("synckey",wechatMeta.getSynckey());
		wx.set("wxsid",wechatMeta.getWxsid());
		wx.set("wxuin",wechatMeta.getWxuin());
		wx.set("pass_ticket",wechatMeta.getPass_ticket());
		wx.set("deviceId",wechatMeta.getDeviceId());
		wx.set("cookie",wechatMeta.getCookie());
		wx.set("baseRequest",wechatMeta.getBaseRequest().toString());
		wx.set("OSyncKey",wechatMeta.getSyncKey().toString());
		wx.set("User",wechatMeta.getUser().toString());
		wx.set("createDate", new Date());
		wx.set("upTime",new Date());
		if(bo){
			wx.save();
		}else {
			wx.update();
		}
		
	}

		/**
		 * wechatMeta:
		 * {"Uin":1008140933,
		 * "UserName":"@7cbe18fbcce99c91d0c0e6bfa2a9bdbf66fbbea2428f5aa28e3ce0cc4d84fb00",
		 * "NickName":"<span class=\"emoji emoji1f6b2\"></span> Cc",
		 * "HeadImgUrl":"/cgi-bin/mmwebwx-bin/webwxgeticon?seq=934412446&username=@7cbe18fbcce99c91d0c0e6bfa2a9bdbf66fbbea2428f5aa28e3ce0cc4d84fb00&skey=@crypt_5f66113c_9edbf53062da3a17765166a2519e6019",
		 * "RemarkName":"",
		 * "PYInitial":"",
		 * "PYQuanPin":"",
		 * "RemarkPYInitial":"",
		 * "RemarkPYQuanPin":"",
		 * "HideInputBarFlag":0,
		 * "StarFriend":0,"Sex":1,
		 * "Signature":"CC",
		 * "AppAccountFlag":0,
		 * "VerifyFlag":0,
		 * "ContactFlag":0,
		 * "WebWxPluginSwitch":0,
		 * "HeadImgFlag":1,"SnsFlag":17}
		 *
		 * 保存微信用户信息
		 * @param wechatMeta
		 */
	@Before(Tx.class)
	public void saveUser(WechatMeta wechatMeta,Integer adminId) {
		Date date = new Date();
		new WxUser()
		.set("Uin",wechatMeta.getUser().getIntValue("Uin"))
		.set("adminId",adminId)
		.set("userNickName",wechatMeta.getUser().getString("NickName"))
		.set("UserName",wechatMeta.getUser().getString("UserName"))
		.set("HeadImgUrl",wechatMeta.getUser().getString("HeadImgUrl"))
		.set("createDate",date)
		.set("isValid",true)
		.set("upTime",date)
		.save();
		WxWechatMeta wx = new WxWechatMeta().findFirst("select wxMetaId from rb_wxwechatmeta where Uin = ? ",wechatMeta.getUser().getIntValue("Uin"));
		boolean bo = false;
		if(wx == null){
			wx = new WxWechatMeta();
			bo = true;
		}
		saveOrUpWx(bo,wx,wechatMeta);
	}
}
