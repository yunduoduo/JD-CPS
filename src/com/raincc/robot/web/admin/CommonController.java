package com.raincc.robot.web.admin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.raincc.interceptor.AdminInterceptor;

@ControllerBind(controllerKey = "/robotAdmin/common", viewPath = "admin/common")
@Before(AdminInterceptor.class)
public class CommonController extends AdminBaseController {
	
	public void page() {
		int curPage = getParaToInt("curPage");
		int totalPage = getParaToInt("totalPage");
		int row = getParaToInt("row");
		int pageSize = getParaToInt("pageSize");
		
		Page page = new Page(null, curPage, pageSize, totalPage, row);
		setAttr("page", page);
		
		render("/WEB-INF/views/admin/common/paginate.html");
	}

}
