package com.raincc.robot.web.admin;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.kit.CommonUtilsKit;
import com.raincc.model.Admin;
import com.raincc.robot.entity.WxSendTimes;
@ControllerBind(controllerKey="/robotAdmin/rob/userConfig",viewPath="admin/robot/userConfig")
@Before(AdminInterceptor.class)
public class WxUserConfigController extends AdminBaseController {
	
	public void setSendTimes(){
		String method = getReqMethod();
		Admin admin = getAdmin();
		if ("post".equalsIgnoreCase(method)) {
			WxSendTimes times = getModel(WxSendTimes.class,"send");
			String IPAddress = CommonUtilsKit.getIpAddr(getRequest());
			WxSendTimes.dao.saveTimes(admin.getInt("adminId"),admin.getStr("account"),times,IPAddress);
			setPageTip("保存成功");
			redirect("setSendTimes");
		}else {
			WxSendTimes times = WxSendTimes.dao.findFirst("select id,GoodsSendTims,PicSendTims,WXSendTimes from rb_wxsendtimes where adminId = ? ",admin.getInt("adminId"));
			setAttr("send",times);
		}
		
	}
}
