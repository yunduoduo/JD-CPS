package com.raincc.kit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.raincc.robot.config.TConstants;

public class CommonUtilsKit {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 获取IP地址
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
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
	
	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @param str
	 * @return
	 */
	public static Date parseDate_yyyymmddhhmmss(String str) {
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 修复double运算精度错误
	 * @param amount
	 * @return
	 */
	public static double fixDoublePrice(double amount) {
		int t = (int) Math.round(amount * 100);
		return (t / 100.0);
	}
	
	/**
	 * 获取完整URL
	 * @param uri
	 * @return
	 */
	public static String fixUrl(String uri) {
		if (uri == null) return "";
		if (uri.toLowerCase().startsWith("http")) {
			return uri;
		}
		return TConstants.website + uri;
	}

}
