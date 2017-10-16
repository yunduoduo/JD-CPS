package com.raincc.robot.jd.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class aa {

	public static void main(String[] args) throws ParseException {
		    String res;
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        long lt = new Long("1501142958000");
	        Date date = new Date(lt);
	        res = simpleDateFormat.format(date);
	        System.err.println(res);
	}
}
