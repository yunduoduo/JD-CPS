package com.raincc.robot.web;

import javax.servlet.http.Cookie;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;

@ControllerBind(controllerKey = "/clear")
public class ClearController extends Controller {
	
	public void index() {
		Cookie[] cs = getCookieObjects();
		for (Cookie c : cs) {
			System.out.println("remove::" + c.getName());
			removeCookie(c.getName());
		}
		getSession().invalidate();
		renderText("OK");
	}

}
