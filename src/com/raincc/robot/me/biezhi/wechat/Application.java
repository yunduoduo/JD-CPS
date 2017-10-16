//package com.raincc.robot.me.biezhi.wechat;
//
//import com.raincc.robot.me.cncoder.record.ClearTask;
//import com.blade.kit.base.Config;
//
//public class Application {
//	
//	public static void main(String[] args) {
//		
//		try {
//			
////			Constant.config = Config.load("classpath:config.properties");
//			
//			WechatRobot wechatRobot = new WechatRobot();
//			wechatRobot.showQrCode();
//			
//			while(!Constant.HTTP_OK.equals(wechatRobot.waitForLogin())){
//				Thread.sleep(3000);
//			}
////			wechatRobot.closeQrWindow();
//			wechatRobot.start();
//			//默认30分钟启动定时删除缓存
////			ClearTask runnable=new ClearTask(30);
////			new Thread(runnable).start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//}