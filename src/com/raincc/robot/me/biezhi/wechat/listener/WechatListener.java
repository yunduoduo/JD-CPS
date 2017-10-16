package com.raincc.robot.me.biezhi.wechat.listener;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.apache.log4j.Logger;


import com.alibaba.fastjson.JSONObject;
//import com.blade.kit.json.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.robot.me.biezhi.wechat.Constant;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;
import com.raincc.robot.me.biezhi.wechat.service.WechatService;

public class WechatListener {

//	private static final Logger _log = LoggerFactory.getLogger(WechatListener.class);
	protected static final Logger _log = Logger.getLogger(WechatListener.class);
	
	int playWeChat = 0;
	
	public void start(final WechatService wechatService, final WechatMeta wechatMeta,final Integer adminId){
		 new Thread(new Runnable() {
			@Override
			public void run() {
					_log.info("进入消息监听模式 ...");
					wechatService.choiceSyncLine(wechatMeta);
					while(true){
						if(Constant.boStop){
							
						int[] arr = wechatService.syncCheck(wechatMeta);
						System.err.println("当前用户登录用户为：UserName="+wechatMeta.getUser().getString("UserName")+",用户NickName="+wechatMeta.getUser().getString("NickName")+",署名为Signature="+wechatMeta.getUser().getString("Signature"));
//						_log.info("retcode={}, selector={}", arr[0], arr[1]);
						_log.info("retcode={"+arr[0]+"}, selector={"+arr[1]+"}");
						Long Uin = wechatMeta.getUser().getLong("Uin");
						if(arr[0] == 1100 || arr[0] == 1101){
							_log.info("你在手机上登出了微信，再见！"+wechatMeta.getUser().getString("UserName"));
							Db.update("update rb_wxuser set isValid = false where Uin = ? ",wechatMeta.getUser().getString("Uin"));
							Db.update("update rb_wxusergroup set isValid = 0,isPutaway = false where Uin = ? ",Uin);
//							Db.update("delete from tmp_flocklist where toUserName = ? ",wechatMeta.getUser().getString("UserName"));
//							c.removeCookie(Constant.CK_WECHATMETA);
							break;
						}
						if(arr[0] == 0){
							if(arr[1] == 2){
								JSONObject data = wechatService.webwxsync(wechatMeta);
								//System.out.println(data.toString());
								wechatService.handleMsg(wechatMeta, data,adminId,Uin);
							} else if(arr[1] == 6){
								JSONObject data = wechatService.webwxsync(wechatMeta);
								wechatService.handleMsg(wechatMeta, data,adminId,Uin);
							} else if(arr[1] == 7){
								playWeChat += 1;
//								_log.info("你在手机上玩微信被我发现了 {} 次", playWeChat);
								_log.info("你在手机上玩微信被我发现了 {"+playWeChat+"} 次");
								wechatService.webwxsync(wechatMeta);
							} else if(arr[1] == 3){
								continue;
							} else if(arr[1] == 0){
								continue;
							}
						} else {
							// 
						}
//						try {
//							_log.info("等待2000ms...");
//							Thread.sleep(2000);
//							
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}else {
						Constant.boStop = true;
						break;
					}
				}
					System.out.println("stopstop");
				
			}
			
		}, "wechat-listener-thread").start();
	}
	
}
