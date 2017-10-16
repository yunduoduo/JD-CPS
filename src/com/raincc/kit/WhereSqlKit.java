package com.raincc.kit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;

public class WhereSqlKit {
	
	StringBuffer where = new StringBuffer();
	List<Object> params = new ArrayList<Object>();
	
	Controller c;
	
	public WhereSqlKit(boolean init) {
		if (init) {
			where.append(" where 1 = ? ");
			params.add(1);
		} else {
			where.append(" and 1 = ? ");
			params.add(1);
		}
	}
	
	public WhereSqlKit(boolean init, Controller c) {
		this(init);
		this.c = c;
	}
	
	public WhereSqlKit(){}
	
	/**
	 * 组装where筛选条件
	 * @param where
	 * @param params
	 * @param whereKey
	 * @param whereCon
	 * @param val
	 */
	public WhereSqlKit append(String whereKey, String whereCon, Object val) {
		if (val == null) {
			return this;
		}
		
		if (val instanceof String && StringUtils.isBlank(val.toString())) {
			return this;
		} else {
			
		}
		
		if (val instanceof Number && ((Number)val).doubleValue() <= 0) {
			return this;
		}
		
		where.append(" and " + whereKey + " " + whereCon + " ? ");
		if ("like".equals(whereCon)) {
			params.add("%" + val+ "%");
		} else {
			params.add(val);
		}
		
		return this;
	}

	public WhereSqlKit append(String whereKey, String whereCon, String paramKey, Class clazz) {
		if (c == null) {
			throw new RuntimeException("调用该方法时需要使用WhereSqlKit(boolean init, Controller c)进行构造");
		}
		Object val = null;
		if (clazz.getSimpleName().equals("Integer")) {
			val = c.getParaToInt(paramKey);
		} else if (clazz.getSimpleName().equals("String")) {
			val = c.getPara(paramKey);
		} else if (clazz.getSimpleName().equals("Long")) {
			val = c.getParaToLong(paramKey);
		} else if (clazz.getSimpleName().equals("Boolean")) {
			val = c.getParaToBoolean(paramKey);
		}
		
		return append(whereKey, whereCon, val);
		
	}
	
	public WhereSqlKit appendStr(String whereKey, String whereCon, String paramKey, String compensate) {
		if (c == null) {
			throw new RuntimeException("调用该方法时需要使用WhereSqlKit(boolean init, Controller c)进行构造");
		}
		String val = c.getPara(paramKey);
		if (StringUtils.isNotBlank(val)) {
			val = val + compensate;
		}
		return append(whereKey, whereCon, val);
	}
	
	public String getSql() {
		return where.toString();
	}
	
	public Object[] getParam() {
		return params.toArray();
	}


}
