package com.raincc.robot.web.admin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.interceptor.WxLoginInterceptor;
import com.raincc.robot.entity.WxUser;
import com.raincc.robot.entity.WxUserGroup;

/**用户信息
 * @author Administrator
 *
 */
@ControllerBind(controllerKey="/robotAdmin/t",viewPath="admin/t")
@Before(AdminInterceptor.class)
public class IMGController extends AdminBaseController {
	public void index(){
		
	}
}
