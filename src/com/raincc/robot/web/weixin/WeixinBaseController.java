package com.raincc.robot.web.weixin;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.raincc.web.BaseController;

public abstract class WeixinBaseController extends BaseController {
	
	//随机生成字符串
	public static String getRandomString(int length){
	    String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    Random random=new Random();
	    StringBuffer sb=new StringBuffer();
	    for(int i=0;i<length;i++){
	      int number=random.nextInt(62);
	      sb.append(str.charAt(number));
	    }
	    return sb.toString();
	}
	//系统前一天
	public static Date getBeforeDay(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        date = calendar.getTime();  
        return date;  
    } 
}
