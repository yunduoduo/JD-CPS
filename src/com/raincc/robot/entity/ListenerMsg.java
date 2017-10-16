package com.raincc.robot.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import sun.util.logging.resources.logging;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.jd.core.jdutils.TUtils;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;

@SuppressWarnings("serial")
@TableBind(tableName="rb_listener_msg",pkName="listenerMsgId")
public class ListenerMsg extends BaseModel<ListenerMsg>{

	public static final  ListenerMsg dao = new ListenerMsg();

	@Before(Tx.class)
	public ListenerMsg saveMsg(WechatMeta wechatMeta,String FromUserName,String GroupName,String content,Integer adminId) {
		String uin = wechatMeta.getUser().getLong("Uin") + "";
		String nickName = wechatMeta.getUser().getString("NickName");
		ListenerMsg listen = new ListenerMsg();
		listen.set("adminId", adminId);
		listen.set("Uin", uin);
		listen.set("NickName",nickName);
		listen.set("FromUserName",FromUserName);
		listen.set("GroupName",GroupName);
		listen.set("content",content);
		String skuId = "";
		String reCont = TUtils.reverse(content);
		Pattern p = Pattern.compile("([0-9]{6,})");
		Matcher m = p.matcher(reCont);
		m.find();
		try {
			skuId = m.group();
			skuId = TUtils.reverse(skuId);
		} catch (Exception e) {
			if(StringUtils.isBlank(skuId)){
				skuId = "0";
			}
		}
		listen.set("skuId",Long.parseLong(skuId));
		listen.set("createDate",new Date());
		listen.save();
		return listen;
	}
	public void getSkuId(String content){
		Pattern p = Pattern.compile("([0-9]{6,})");
		Matcher m = p.matcher(content);
		m.find();
		String skuId = "";
		try {
			skuId = m.group();
			content = content.substring(content.indexOf(skuId));
		} catch (Exception e) {
			if(StringUtils.isBlank(skuId)){
				skuId = "0";
			}
		}
		
	}
	/**自动转链接
	 * @param uin  微信用户uin
	 * @param listenerMsgId  采集消息主键
	 * @param unionId  //联盟Id
	 */
	public void toLink(final Long uin, final Integer listenerMsgId,final Long unionId,final Integer adminId) {
		List<WxUserGroup> g = WxUserGroup.dao.find(" select distinct c.jdChannelId from rb_wxusergroup u "
				+ " LEFT JOIN rb_jdchannel c on c.channelId = u.channelId where u.isValid = 2 ");
//				/*+ "  where u.Uin = ? ",uin*/);
		String access_token = "";
		JdPermission jp = JdPermission.dao.findFirst("select JD_AccessToken from rb_jdpermission where unionId = ? and tokenValidTime > UNIX_TIMESTAMP() ",TConstants.BL_JD_UNIONID);
		if(jp != null && StringUtils.isNotBlank(jp.getStr("JD_AccessToken"))){
			access_token = jp.getStr("JD_AccessToken");
		}
		ListenerMsg lMsg = ListenerMsg.dao.findById(listenerMsgId);
		Integer count = 0;
		if(lMsg != null){
			if(StringUtils.isNotBlank(access_token)){
				for (WxUserGroup jdchannel : g) {
					Integer jdChannelId = jdchannel.getInt("jdChannelId");
					if(unionId == null || unionId <= 0){
						_log.info("转链接失败。。原因是unionId = "+unionId);
						return;
					}
					String skuIds = lMsg.getLong("skuId") +"";
					JSONObject netJoson = new JSONObject();
					netJoson.put("materialIds",skuIds);
					netJoson.put("proCont",1);
					netJoson.put("positionId",jdChannelId);
					netJoson.put("subUnionId",unionId);
					
					String body = TUtils.getJdParams(unionId,access_token, TConstants.JD_METHOD_getCodeBySubUnionId, netJoson);
					_log.info(body);
					if(!body.contains("error_response") && StringUtils.isNotBlank(body)){
						JSONObject jsonObject = JSONObject.fromObject(body);
						JSONObject j = jsonObject.getJSONObject("jingdong_service_promotion_wxsq_getCodeBySubUnionId_responce");
						JSONObject a = j.getJSONObject("getcodebysubunionid_result");
						String resultCode = a.getString("resultCode");
						String resultMessage = a.getString("resultMessage");
						if("0".equals(resultCode) && resultMessage.contains("成功")){
							_log.info("resultCode="+resultCode+",resultMessage="+resultMessage);
							JSONObject urlList = a.getJSONObject("urlList");
						       Iterator iterator = urlList.keys();
						       while(iterator.hasNext()){
					              String key = (String) iterator.next();
					              String value = urlList.getString(key);
					              LinksMsg.dao.saveLikesMsg(access_token,listenerMsgId,value,unionId,jdChannelId,key,adminId,true);
					              count ++;
						       }
						}
						_log.info("成功转链");
					}else {
						_log.info("转链接失败。。原因是:"+body);
					}
				}
				if(count > 0){
					Db.update("update rb_listener_msg set isLinks = true where listenerMsgId = ? ",listenerMsgId);
					_log.info("此消息一共转了"+count+"个链接");
				}
			}else {
				_log.info("转链接失败。。原因是access_token为空");
			}
		}
		
	}
}
