package com.raincc.robot.web.admin;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.robot.entity.LogType;
import com.raincc.robot.entity.UserLogs;

@ControllerBind(controllerKey="/robotAdmin/rob/adminLog",viewPath="admin/robot/adminLog")
@Before(AdminInterceptor.class)
public class AdminLogRecordController extends AdminBaseController {

	public void findLogs(){
		Integer pageNo = getParaToInt(0,1);
		StringBuffer buffer = new StringBuffer();
		buffer.append(" where 1 = ? ");
		List<Object> list = new ArrayList<Object>();
		list.add(1);
		String userName = getPara("userName");
		if(StringUtils.isNotBlank(userName)){
			buffer.append(" and lo.userName = ? ");
			list.add(userName);
		}
		Integer logTypeId = getParaToInt("logTypeId");
		if(logTypeId != null && logTypeId > 0){
			buffer.append(" and lo.logTypeId = ? ");
			list.add(logTypeId);
		}
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate)){
			buffer.append(" and lo.createDate between ? and ? ");
			list.add(startRegDate);
			list.add(endRegDate);
		}
		Page<UserLogs> page = UserLogs.dao.paginate(pageNo, 20," select lo.logId,lo.adminId,lo.userName,lo.IPAddress,lo.logInfo,lo.createDate,t.typeName ",
				" from rb_user_logs lo "
				+" left join rb_logtype t on t.logTypeId = lo.logTypeId "+buffer.toString()
				+" order by lo.logId desc ",list.toArray());
		setAttr("page", page);
		List<LogType> logType = LogType.dao.findAll(true);
		setAttr("logType",logType);
	}
}
