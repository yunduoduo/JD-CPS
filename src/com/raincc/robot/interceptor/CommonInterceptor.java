package com.raincc.robot.interceptor;

import org.apache.log4j.Logger;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;

public class CommonInterceptor implements Interceptor {
	
	protected static final Logger _log = Logger.getLogger(CommonInterceptor.class);

	@Override
	public void intercept(ActionInvocation ai) {
		long start = System.currentTimeMillis();
		ai.invoke();
		_log.info(ai.getActionKey() + " " + (System.currentTimeMillis() - start) + "ms");
	}

}
