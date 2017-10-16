package com.raincc.kit;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;

public class IpKit {
	
	public static Logger _log = Logger.getLogger(IpKit.class);
	
	public static String getRealIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	public static String getIpAddr_last(HttpServletRequest request) {
		String ip = getRealIp(request);
		if (ip.contains(",")) {
			return ip.split(",")[ip.split(",").length-1].trim();
		}
		return ip;
	}
	
	public static String getIpAddr_first(HttpServletRequest request) {
		String ip = getRealIp(request);
		if (ip.contains(",")) {
			return ip.split(",")[0].trim();
		}
		return ip;
	}
	
	public static String getRealIpV2(HttpServletRequest request) {
		String accessIP = request.getHeader("x-forwarded-for");
        if (null == accessIP)
            return request.getRemoteAddr();
        return accessIP;
	}
	
	private static final String ipAddress = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip={ip}";
	
	public static String getIpAddress(HttpServletRequest request) {
		try {
			String ip = getIpAddr_first(request);
			String requestUrl = ipAddress.replace("{ip}", ip);
			String json = HttpKit.get(requestUrl);
			JSONObject jo = JSONObject.parseObject(json);
			if (jo.getInteger("ret") == 1) {
				String city = jo.getString("city");
				return city;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			_log.info("获取IP失败：" + e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) {
//		System.out.println(IpKit.getIpAddress("121.199.56.174"));
	}

}
