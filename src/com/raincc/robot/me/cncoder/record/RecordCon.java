package com.raincc.robot.me.cncoder.record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.raincc.robot.me.biezhi.wechat.Constant;
import com.raincc.robot.me.biezhi.wechat.model.WechatMeta;

//import com.blade.kit.json.JSONObject;

public class RecordCon {

	//记录上一次自动回复的联系人数组列表,20分钟清空一次
	public static List<String> cache = new ArrayList<String>();
	
	public void writeCon(WechatMeta wechatMeta){
		final File conn = new File("record/"+wechatMeta.getUser().getString("UserName")+".json");
		
		if(!conn.exists()){
			 //先得到文件的上级目录，并创建上级目录，在创建文件
		    conn.getParentFile().mkdir();
		    try {
		        //创建文件
		        conn.createNewFile();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		
//		//创建文件
//		//wechatMeta.getUser().getString("UserName")+".json"
//		if(!conn.exists()){
//			conn.mkdirs();
//		}
//			try {
//				 new File("record/"+wechatMeta.getUser().getString("UserName")+".json").createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		
			FileWriter writer;
			try {
				writer = new FileWriter(conn);
				 String line = System.getProperty("line.separator");
				 int count=0;
				 for (int i = 0, len = Constant.CONTACT.getMemberList().size(); i < len; i++) {
						JSONObject member = Constant.CONTACT.getMemberList().getJSONObject(i);
						// 公众号/服务号
						if (member.getIntValue("VerifyFlag") == 24||member.getIntValue("VerifyFlag") == 8) {
							continue;
						}
						// 特殊联系人
						if (Constant.FILTER_USERS.contains(member.getString("UserName"))) {
							continue;
						}
						// 群聊
						if (member.getString("UserName").indexOf("@@") != -1) {
							continue;
						}
						
						//写入UserName NickName RemarkName Signature
						writer.write(member.getString("RemarkName")+"   "+member.getString("NickName")+"  "+member.getString("UserName")+"\r\n");
						writer.write(line);
						writer.write(member.getString("Signature"));
						writer.flush();
						writer.write(line);
						count++;
					}
			System.out.println(count);	
			writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

	}
	
}


