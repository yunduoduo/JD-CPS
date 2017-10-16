package com.raincc.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.JdPermission;
import com.raincc.wx.util.HttpKit;

public class JdRefreshTokenTask implements Job {
	private static final Logger _log = Logger.getLogger(JdRefreshTokenTask.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		_log.info("执行job.....");
		Integer time = 10 * 60;
		JdPermission jp = JdPermission.dao.findFirst("select refresh_token from rb_jdpermission where unionId = ? and (tokenValidTime - UNIX_TIMESTAMP() <= ?) ",TConstants.BL_JD_UNIONID,time);
		if(jp != null && StringUtils.isNotBlank(jp.getStr("refresh_token"))){
			String url = TConstants.JD_REFRESH_TOKEN_Url
					.replace("{client_id}", TConstants.JD_Appkey)
					.replace("{client_secret}",TConstants.JD_AppSecret)
					.replace("{refresh_token}", jp.getStr("refresh_token"));
			JSONObject jsonObject = HttpKit.httpRequest(url, "GET", null);
			if(jsonObject != null){
					Integer expires_in = jsonObject.getInteger("expires_in");
					Integer tokenValidTime = (int)(new Date().getTime() / 1000) + expires_in;
					Db.update("update rb_jdpermission set JD_AccessToken = ? ,upTime = ? ,tokenValidTime = ? ,user_nick = ? ,expires_in = ? ,refresh_token = ?,time=?,token_type=? where unionId = ? ",
							jsonObject.getString("access_token"),new Date(),tokenValidTime,jsonObject.getString("user_nick"),jsonObject.getInteger("expires_in"),jsonObject.getString("refresh_token"),
							jsonObject.getString("time"),jsonObject.getString("token_type"),TConstants.BL_JD_UNIONID);
				}
			}
		}

}
