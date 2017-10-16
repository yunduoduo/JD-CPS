package com.raincc.robot.entity;

import com.jfinal.ext.plugin.tablebind.TableBind;

@SuppressWarnings("serial")
@TableBind(tableName="rb_wxwechatmeta",pkName="wxMetaId")
public class WxWechatMeta extends BaseModel<WxWechatMeta> {

	public static final WxWechatMeta dao = new WxWechatMeta();
}
