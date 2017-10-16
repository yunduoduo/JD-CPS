//package com.raincc.robot.entity;
//
//import com.jfinal.aop.Before;
//import com.jfinal.ext.plugin.tablebind.TableBind;
//import com.jfinal.plugin.activerecord.tx.Tx;
//import com.raincc.robot.me.biezhi.wechat.Constant;
//import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;
//
//@SuppressWarnings("serial")
//@TableBind(tableName="tmp_flocklist",pkName="id")
//public class TmpFlockList extends BaseModel<TmpFlockList> {
//
//	public static final TmpFlockList dao = new TmpFlockList();
//
//	@Before(Tx.class)
//	public TmpFlockList flockSave(WechatMeta wechatMeta) {
//		TmpFlockList t = new TmpFlockList();
//		t.set("Uin",wechatMeta.getUser().getLong("Uin")+"");
//		t.set("toUserName",wechatMeta.getUser().getString("UserName"));
//		t.set("NickName", wechatMeta.getUser().getString("NickName"));
//		t.set("flockList", Constant.CONTACT.getContactList().toString());
//		t.save();
//		return t;
//	}
//}
