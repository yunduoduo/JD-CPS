package com.raincc.robot.web.admin;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.raincc.interceptor.AdminInterceptor;
import com.raincc.interceptor.JdLoginInterceptor;
import com.raincc.robot.config.TConstants;
import com.raincc.robot.entity.JdImportOrderSkus;
import com.raincc.robot.entity.JdImportOrders;
import com.raincc.robot.entity.WxJdUser;
import com.raincc.robot.jd.core.jdutils.TUtils;
import com.raincc.robot.util.ExcelUtil;
import com.raincc.robot.util.HttpRequestUtils;

@ControllerBind(controllerKey="/robotAdmin/rob/jd/order",viewPath="admin/robot/jd/order")
@Before({AdminInterceptor.class,JdLoginInterceptor.class})
public class JDOrderController extends AdminBaseController {
	
	/**
	 * 业绩订单
	 */
	public void performanceOrder(){
		Integer pageNo = getParaToInt(0,1);
		Long orderId = getParaToLong("orderId");
		StringBuffer buffer = new StringBuffer();
		buffer.append(" where 1= ? ");
		List<Object> findArray = new ArrayList<Object>();
		findArray.add(1);
		buffer.append(" and mp.orderStatusName = ? ");
		findArray.add("已完成");
		String shortDays = getPara("shortDays");
		String balance = getPara("balance");
		Long unionsIds = getParaToLong("unionsIds");
		if(unionsIds == null){
			unionsIds = getJDUnionId();
		}
		Boolean bo = getIndividual();//判断是否是个人用户
		if(bo){
			buffer.append(" and mp.subUnionId = ? ");
			findArray.add(unionsIds);
		}else {
			if(getBoJDChannel()){//主账户
				buffer.append(" and mp.subUnionId = ? ");
				findArray.add(TConstants.BL_JD_UNIONID);
			}else {//子账户
				buffer.append(" and mp.channelUnionId = ? ");
				findArray.add(unionsIds);
			}
		}
		
		
		if(StringUtils.isNotBlank(balance)){
			buffer.append(" and mp.balance = ? ");
			findArray.add(Integer.parseInt(balance));
		}
		if(getBoJDChannel()){
			buffer.append(" and mp.subUnionId = ? ");
			findArray.add(TConstants.BL_JD_UNIONID);
		}else {
			buffer.append(" and mp.channelUnionId = ? ");
			findArray.add(getJDUnionId());
		}
		if(orderId !=null && orderId > 0){
			buffer.append(" and mp.orderId = ? ");
			findArray.add(orderId);
		}
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		Date date = new Date();
		String staString = TUtils.getAfterDate(date,-30);
		String endString = TUtils.getAfterDate(date,1);
		if(StringUtils.isBlank(shortDays)){
			if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate) ){
				buffer.append(" and mp.orderTime between ? and ? ");
				findArray.add(startRegDate);
				findArray.add(endRegDate);
			}else if(StringUtils.isBlank(startRegDate) && StringUtils.isBlank(endRegDate) ){
				buffer.append(" and mp.orderTime between ? and ? ");
				findArray.add(staString);
				findArray.add(endString);
			}
		}else if(StringUtils.isNotBlank(shortDays)){
			buffer.append(" and mp.orderTime between ? and ? ");
			findArray.add(TUtils.getAfterDate(date,Integer.parseInt(shortDays)));
			findArray.add(TUtils.getAfterDate(date,1));
		}
		setAttr("startRegDate", staString);
		setAttr("endRegDate",endString);
		String yn = getPara("yn");
		if(StringUtils.isNotBlank(yn)){
			buffer.append(" and mp.yn = ? ");
			findArray.add(Integer.parseInt(yn));
		}
		String urlString = " mp.id,mp.parentId,mp.subUnionId,mp.channelUnionId,mp.spName,mp.balance,mp.balanceName,mp.commission,mp.cosPrice,"
				+ " mp.orderTime,mp.finishTime,mp.orderId,mp.sourceEmt,mp.splitType,mp.totalMoney,mp.yn,mp.plus,mp.popId,mp.splitName,mp.orderStatusName ";
		String skusUrl = " sk.skusId,sk.commission,sk.commissionRate,sk.price,sk.skuName,sk.skuPrice,sk.skuNum,sk.skuReturnNum,sk.subSideRate, "
				+ " sk.subsidyRate,sk.valid,sk.splitName,sk.ruleType,sk.orderStatusName,sk.balance,sk.subUnionId ";
		String download = getPara("download");
		if(download != null && download.endsWith("1")){
			String doString = " mp.orderId,sku.skuName,sku.skuPrice,sku.skuNum,sku.skuReturnNum,sku.commissionRate,"
					+ " sku.subSideRate,sku.commission,mp.balance,mp.orderTime,mp.finishTime,mp.orderStatusName,mp.splitName,"
					+ " mp.yn,mp.spName,mp.plus ";
			List<JdImportOrders> aList = JdImportOrders.dao.find("select "+doString+
					 " from rb_jd_importorders mp left join rb_jd_importorder_skus sku "
					 + " on sku.importOrderId = mp.id " +buffer.toString()
					 + " order by mp.orderTime desc ",findArray.toArray());
			String[] asStrings = {"订单号","商品名称","商品金额","商品数量","退货数量","佣金比例",
					"分成比例","预估收入","是否结算","下单时间","完成时间","订单状态","是否拆单",
					"是否有效","推广位名称","是否是会员PLUS"};
			ArrayList<String> nameFeild = new ArrayList<String>();
			nameFeild.add("orderId");
			nameFeild.add("skuName");
			nameFeild.add("skuPrice") ;
			nameFeild.add("skuNum");
			nameFeild.add("skuReturnNum");
			nameFeild.add("commissionRate");
			
			nameFeild.add("subSideRate");
			nameFeild.add("commission");
			nameFeild.add("balance");
			nameFeild.add("orderTime");
			nameFeild.add("finishTime");
			nameFeild.add("orderStatusName");
			nameFeild.add("splitName");
			nameFeild.add("yn");
			nameFeild.add("spName");
			nameFeild.add("plus");
			
			ExcelUtil<JdImportOrders> eUtil = new ExcelUtil<JdImportOrders>();
			String titleString = "业绩订单";
			String tim = ""+System.currentTimeMillis();
			String downFile = "/upload/downFile/";
			eUtil.createExcelSheet(aList, titleString+tim, nameFeild,asStrings,downFile);
			System.out.println(PathKit.getWebRootPath()+downFile+titleString+tim+".xls");
			renderFile(new File(PathKit.getWebRootPath()+downFile+ titleString+tim+".xls"));
			return;
		}
		
		Page<JdImportOrders> page = JdImportOrders.dao.paginate(pageNo, 20,"select " + urlString,
			" from rb_jd_importorders mp "+buffer.toString()+
			" order by mp.orderTime desc "
			,findArray.toArray());
		for (JdImportOrders orders : page.getList()) {
			Integer id = orders.getInt("id");
			List<JdImportOrderSkus> list = JdImportOrderSkus.dao.find("select "+skusUrl+" from rb_jd_importorder_skus sk where sk.importOrderId = ? ",id);
			JdImportOrderSkus skuNums = JdImportOrderSkus.dao.findFirst("select sum(skuNum) skuNums,sum(skuReturnNum) skuReturnNum from rb_jd_importorder_skus  where importOrderId = ? ",id);
			orders.put("skuNums",skuNums.getBigDecimal("skuNums").intValue());
			orders.put("skuReturnNum", skuNums.getBigDecimal("skuReturnNum").intValue());
			orders.put("skusList",list);
		}
		setAttr("page",page);
		WxJdUser jdUser = WxJdUser.dao.findFirst("select unionId,JXJunionId from rb_wxjduser where unionId = ? ",getJDUnionId());
		setAttr("jdUser",jdUser);
		
	}

	/**
	 * 引入订单
	 */
	public void findImpOrder(){
		Integer pageNo = getParaToInt(0,1);
		Long orderId = getParaToLong("orderId");
		StringBuffer buffer = new StringBuffer();
		buffer.append(" where 1= ? ");
		List<Object> findArray = new ArrayList<Object>();
		findArray.add(1);
		
		String shortDays = getPara("shortDays");
		Boolean bo = getIndividual();//判断是否是个人用户
		Long unionsIds = getParaToLong("unionsIds");
		if(unionsIds == null){
			unionsIds = getJDUnionId();
		}
		if(bo){
			buffer.append(" and mp.subUnionId = ? ");
			findArray.add(unionsIds);
		}else {
			if(getBoJDChannel()){//主账户
				buffer.append(" and mp.subUnionId = ? ");
				findArray.add(TConstants.BL_JD_UNIONID);
			}else {//子账户
				buffer.append(" and mp.channelUnionId = ? ");
				findArray.add(unionsIds);
			}
		}
		
		
		if(orderId !=null && orderId > 0){
			buffer.append(" and mp.orderId = ? ");
			findArray.add(orderId);
		}
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		Date date = new Date();
		String staString = TUtils.getAfterDate(date,-30);
		String endString = TUtils.getAfterDate(date,1);
		if(StringUtils.isBlank(shortDays)){
			if(StringUtils.isNotBlank(startRegDate) && StringUtils.isNotBlank(endRegDate) ){
				buffer.append(" and mp.orderTime between ? and ? ");
				findArray.add(startRegDate);
				findArray.add(endRegDate);
			}else if(StringUtils.isBlank(startRegDate) && StringUtils.isBlank(endRegDate) ){
				buffer.append(" and mp.orderTime between ? and ? ");
				findArray.add(staString);
				findArray.add(endString);
			}
		}else if(StringUtils.isNotBlank(shortDays)){
			buffer.append(" and mp.orderTime between ? and ? ");
			findArray.add(TUtils.getAfterDate(date,Integer.parseInt(shortDays)));
			findArray.add(TUtils.getAfterDate(date,1));
		}
		setAttr("startRegDate", staString);
		setAttr("endRegDate",endString);
		String yn = getPara("yn");
		if(StringUtils.isNotBlank(yn)){
			buffer.append(" and mp.yn = ? ");
			findArray.add(Integer.parseInt(yn));
		}
		String urlString = " mp.id,mp.parentId,mp.subUnionId,mp.channelUnionId,mp.spName,mp.balance,mp.balanceName,mp.commission,mp.cosPrice,"
				+ " mp.orderTime,mp.finishTime,mp.orderId,mp.sourceEmt,mp.splitType,mp.totalMoney,mp.yn,mp.plus,mp.popId,mp.splitName,mp.orderStatusName ";
		String skusUrl = " sk.skusId,sk.commission,sk.commissionRate,sk.price,sk.skuName,sk.skuPrice,sk.skuNum,sk.skuReturnNum,sk.subSideRate, "
				+ " sk.subsidyRate,sk.valid,sk.splitName,sk.ruleType,sk.orderStatusName,sk.balance,sk.subUnionId ";
		
		String download = getPara("download");
		if(download != null && download.endsWith("1")){
			String doString = " mp.orderId,sku.skuName,sku.skuPrice,sku.skuNum,sku.skuReturnNum,sku.commissionRate,"
					+ " sku.subSideRate,sku.commission,mp.balance,mp.orderTime,mp.finishTime,mp.orderStatusName,mp.splitName,"
					+ " mp.yn,mp.spName,mp.plus ";
			List<JdImportOrders> aList = JdImportOrders.dao.find("select "+doString+
					 " from rb_jd_importorders mp left join rb_jd_importorder_skus sku "
					 + " on sku.importOrderId = mp.id " +buffer.toString()
					 + " order by mp.orderTime desc ",findArray.toArray());
			String[] asStrings = {"订单号","商品名称","商品金额","商品数量","退货数量","佣金比例",
					"分成比例","预估收入","是否结算","下单时间","完成时间","订单状态","是否拆单",
					"是否有效","推广位名称","是否是会员PLUS"};
			ArrayList<String> nameFeild = new ArrayList<String>();
			nameFeild.add("orderId");
			nameFeild.add("skuName");
			nameFeild.add("skuPrice") ;
			nameFeild.add("skuNum");
			nameFeild.add("skuReturnNum");
			nameFeild.add("commissionRate");
			
			nameFeild.add("subSideRate");
			nameFeild.add("commission");
			nameFeild.add("balance");
			nameFeild.add("orderTime");
			nameFeild.add("finishTime");
			nameFeild.add("orderStatusName");
			nameFeild.add("splitName");
			nameFeild.add("yn");
			nameFeild.add("spName");
			nameFeild.add("plus");
			
			ExcelUtil<JdImportOrders> eUtil = new ExcelUtil<JdImportOrders>();
			String titleString = "引入订单";
			String tim = ""+System.currentTimeMillis();
			String downFile = "/upload/downFile/";
			eUtil.createExcelSheet(aList, titleString+tim, nameFeild,asStrings,downFile);
			System.out.println(PathKit.getWebRootPath()+downFile+titleString+tim+".xls");
			renderFile(new File(PathKit.getWebRootPath()+downFile+ titleString+tim+".xls"));
			return;
		}
		Page<JdImportOrders> page = JdImportOrders.dao.paginate(pageNo, 20,"select " + urlString,
			" from rb_jd_importorders mp "+buffer.toString()+
			" order by mp.orderTime desc "
			,findArray.toArray());
		for (JdImportOrders orders : page.getList()) {
			Integer id = orders.getInt("id");
			List<JdImportOrderSkus> list = JdImportOrderSkus.dao.find("select "+skusUrl+" from rb_jd_importorder_skus sk where sk.importOrderId = ? ",id);
			JdImportOrderSkus skuNums = JdImportOrderSkus.dao.findFirst("select sum(skuNum) skuNums from rb_jd_importorder_skus  where importOrderId = ? ",id);
			orders.put("skuNums",skuNums.getBigDecimal("skuNums").intValue());
			orders.put("skusList",list);
		}
		setAttr("page",page);
		WxJdUser jdUser = WxJdUser.dao.findFirst("select unionId,JXJunionId from rb_wxjduser where unionId = ? ",getJDUnionId());
		setAttr("jdUser",jdUser);
	}
	
	
	/**
	 * 刷新订单
	 */
	public void refreshOrder(){
		String orderId = getPara("orderId");
		String startRegDate = getPara("startRegDate");
		String endRegDate = getPara("endRegDate");
		String shortDays = getPara("shortDays");
		Date date = new Date();
		if(StringUtils.isNotBlank(shortDays)){
			startRegDate = TUtils.getAfterDate(date,Integer.parseInt(shortDays));
			endRegDate = TUtils.getAfterDate(date,0);
		}
		
		List<String> list = TUtils.getStringDateList(startRegDate, endRegDate);
		if(list.size() > 33){
			renderTemplateJson("-1", "刷新订单太多");
			return;
		}
		Boolean bo = getIndividual();//判断是否是个人用户
		if(list.size() > 0){
//			API接口
			impOrder(orderId,list,bo);
//			京东接口
			commissionOrders(orderId,startRegDate,endRegDate,bo);
		}else {
			renderTemplateJson("-1", "日期错误");
			return;
		}
		renderTemplateJson("0", "刷新成功");
		return;
	}
	
	
	/**京东接口刷新
	 * @param orderId
	 * @param startRegDate
	 * @param endRegDate
	 */
	private void commissionOrders(String orderId, String startRegDate,String endRegDate,boolean bo) {
			Integer count = 1;
			Integer conData = 0;
			Integer co = 0;
			if(StringUtils.isBlank(orderId)){
				orderId = "";
			}
			if(StringUtils.isBlank(startRegDate)){
				startRegDate = TUtils.getDateYMdHms("yyyy-MM-dd",new Date());
			}
			if(StringUtils.isBlank(endRegDate)){
				endRegDate = TUtils.getDateYMdHms("yyyy-MM-dd",new Date());
			}
			String cookie = "";
			if(bo){
				cookie = WxJdUser.dao.findFirst("select cookie from rb_wxjduser where unionId = ? ",getJDUnionId()).getStr("cookie");
			}else {
				cookie = getBLJDCookies();
			}
			while (true) {
				try {
				JSONObject  data = new JSONObject();
				JSONObject j1 = new JSONObject();
				j1.put("total",0);
				j1.put("pageNum",count);
				j1.put("size",20);
				data.put("pagination", j1);
				data.put("order","[]");
				data.put("data", "[]");
				List<String> list = new ArrayList<String>();
				JSONObject j2 = new JSONObject();
				j2.put("name","orderStatus");//查询是否有效，有效value是1，全部值为空
				j2.put("value","");
				list.add(j2.toString());
				JSONObject j3 = new JSONObject();
				j3.put("name","accountDateStr");
				j3.put("value",startRegDate+"#"+endRegDate);
				list.add(j3.toString());
				JSONObject shortcutDate = new JSONObject();
				shortcutDate.put("name","shortcutDate");//快捷时间，今天value=0，昨天是1，过去7天就是7
				shortcutDate.put("value","");
				list.add(shortcutDate.toString());
				
				JSONObject orderjo = new JSONObject();
				orderjo.put("name","orderId");
				orderjo.put("value",orderId);
				list.add(orderjo.toString());
				
				data.put("search", list);
				String body = HttpRequestUtils.sendHttpUrl(TConstants.JD_PY_ImportOrders, data.toString(),cookie,TConstants.ContentType_PAYLOAD);
				if(StringUtils.isNotBlank(body) && !body.contains("<body")){
					JSONObject jsonObject = JSONObject.fromObject(body);
					JSONArray dataArray = jsonObject.getJSONArray("data");
					_log.info("结果有"+dataArray.size()+"数据。");
					conData += dataArray.size();
					if(dataArray.size() > 0){
						for (int i = 0; i < dataArray.size(); i++) {
							JSONObject jo = dataArray.getJSONObject(i);
							synchronized (JDOrderController.class) {
								if(bo){
									JdImportOrders.dao.individual(jo);
								}else {
									JdImportOrders.dao.crawling(jo);
								}
							}
						}
						if(StringUtils.isNotBlank(orderId)){
							renderText("一共执行了"+conData+"数据。body:"+body);
							return;
						}
						count ++;
						Thread.sleep(1 * 1000);
					}else {
						renderText("一共执行了"+conData+"数据。body:"+body);
						return;
					}
				}else {
					if(co >= 1){
						renderText("一共执行了"+conData+"数据。body:"+body);
						return;
					}
					co ++;
				}
				} catch (Exception e) {
					renderText("一共执行了"+conData+"数据。");
					return;
				}
			}
		
	}

	/**刷新API订单
	 * @param orderId
	 * @param list
	 */
	private void impOrder(String orderId, List<String> list,boolean bo) {
		if(bo || StringUtils.isNotBlank(orderId)){
			return;
		}
		for (String stringDate : list) {
			stringDate = stringDate.replace("-","").replace(":", "").replace(" ", "");
			JSONObject netJoson = new JSONObject();
			Long unionId = getJDUnionId();
			netJoson.put("unionId",unionId);
			netJoson.put("time",stringDate);
			netJoson.put("orderId",orderId);
			netJoson.put("pageIndex",1);
			netJoson.put("pageSize",999999999);
			String tokenString = getTAccessToken();
//			stringDate = "5d1045ab-e95f-4254-88c6-b6c35581f3bf";
			String body = TUtils.getJdInterface(tokenString, TConstants.JD_METHOD_getQueryImportOrders, netJoson);
			if(body.contains("error_response") || StringUtils.isBlank(body)){
				renderTemplateJson("-1",body);
				return;
			}
			System.err.println(body);
			JSONObject responceBody = JSONObject.fromObject(body);
			JSONObject responce = responceBody.getJSONObject("jingdong_UnionService_queryImportOrders_responce");
			JSONObject jsonObject = responce.getJSONObject("queryImportOrders_result");
			JSONArray data = null;
			if(jsonObject.toString().contains("data")){
				data = jsonObject.getJSONArray("data");
				_log.info("获取到"+data.size()+"数据。");
				for (int i = 0; i < data.size(); i++) {
					JSONObject j = data.getJSONObject(i);
					synchronized (JDOrderController.class) {
						JdImportOrders.dao.saveOrder(j,unionId);
					}
				}
			}
		}
		
	}

	/**
	 *	调用京东接口订单 
	 */
	public void impOrder() {
		Integer dates = 0;
		String time = getPara("time");
		if(StringUtils.isBlank(time)){
			time = TUtils.getDateYMdHms("yyyyMMdd",new Date());
		}else{
			time = time.replace("-","").replace(":", "").replace(" ", "");
		}
		if(time.length() == 6){
			Integer year = Integer.parseInt(time.substring(0,4));
			Integer month = Integer.parseInt(time.substring(4,6));
			dates = TUtils.getDays(year,month);
		}else {
			dates = 1;
		}
		for (int q = 1; q <= dates; q++) {
			System.err.println("第"+q+"天循环。。");
			JSONObject netJoson = new JSONObject();
			Long unionId = TConstants.BL_JD_UNIONID;
			netJoson.put("unionId",unionId);
			if(time.length() == 6){
				String daString = q > 10 ? (""+q) : ("0" + q);
				netJoson.put("time",time+daString);
			}else {
				netJoson.put("time",time);
			}
			netJoson.put("pageIndex",1);
			netJoson.put("pageSize",999999999);
			String body = TUtils.getJdInterface(getTAccessToken(), TConstants.JD_METHOD_getQueryImportOrders, netJoson);
			if(body.contains("error_response") || StringUtils.isBlank(body)){
				renderTemplateJson("-1",body);
				return;
			}
			JSONObject responceBody = JSONObject.fromObject(body);
			JSONObject responce = responceBody.getJSONObject("jingdong_UnionService_queryImportOrders_responce");
			JSONObject jsonObject = responce.getJSONObject("queryImportOrders_result");
			JSONArray data = null;
			if(jsonObject.toString().contains("data")){
				data = jsonObject.getJSONArray("data");
				_log.info("获取到"+data.size()+"数据。");
//				renderText(body);
//				return;
				for (int i = 0; i < data.size(); i++) {
					JSONObject j = data.getJSONObject(i);
					synchronized (JDOrderController.class) {
						JdImportOrders.dao.saveOrder(j,unionId);
					}
				}
				renderText(data.toString());
			}else {
				renderNull();
			}
		}
		
	}
	
	
	/**
	 * 爬取京东订单
	 */
	public void commissionOrders(){
		Integer count = 1;
		Integer conData = 0;
		Integer co = 0;
		String staTime = getPara("startTime");
		String endTime = getPara("endTime");
		String orderIdPara = getPara("orderId");
		if(StringUtils.isBlank(orderIdPara)){
			orderIdPara = "";
		}
		if(StringUtils.isBlank(endTime)){
			endTime = TUtils.getDateYMdHms("yyyy-MM-dd",new Date());
		}
		if(StringUtils.isBlank(staTime)){
			staTime = TUtils.getDateYMdHms("yyyy-MM-dd",new Date());
		}
		while (true) {
			try {
			String cookie = getBLJDCookies();
			JSONObject  data = new JSONObject();
			JSONObject j1 = new JSONObject();
			j1.put("total",0);
			j1.put("pageNum",count);
			j1.put("size",20);
			data.put("pagination", j1);
			data.put("order","[]");
			data.put("data", "[]");
			List<String>list = new ArrayList<String>();
			JSONObject j2 = new JSONObject();
			j2.put("name","orderStatus");//查询是否有效，有效value是1，全部值为空
			j2.put("value","");
			list.add(j2.toString());
			JSONObject j3 = new JSONObject();
			j3.put("name","accountDateStr");
			j3.put("value",staTime+"#"+endTime);
			list.add(j3.toString());
			JSONObject shortcutDate = new JSONObject();
			shortcutDate.put("name","shortcutDate");//快捷时间，今天value=0，昨天是1，过去7天就是7
			shortcutDate.put("value","");
			list.add(shortcutDate.toString());
			
			JSONObject orderId = new JSONObject();
			orderId.put("name","orderId");
			orderId.put("value",orderIdPara);
			list.add(orderId.toString());
			
			data.put("search", list);
			
			String body = HttpRequestUtils.sendHttpUrl(TConstants.JD_PY_ImportOrders, data.toString(),cookie,TConstants.ContentType_PAYLOAD);
			
			
//			if(StringUtils.isBlank(body)){//有可能是cookie失效，拿一次cookie
//				Map<String,String> maps = TUtils.getCookieToLogin(TConstants.T_LOGINNAME, TConstants.T_PASSWORD);
//				String code = maps.get("code");
//				if("0".equals(code)){
//					cookie = maps.get("info");
//					body = HttpRequestUtils.sendHttpUrl(TConstants.JD_PY_ImportOrders, data.toString(),cookie,TConstants.ContentType_PAYLOAD);
//				}
//			}
			if(StringUtils.isNotBlank(body) && !body.contains("<body")){
				JSONObject jsonObject = JSONObject.fromObject(body);
				JSONArray dataArray = jsonObject.getJSONArray("data");
				_log.info("结果有"+dataArray.size()+"数据。");
				conData += dataArray.size();
				if(dataArray.size() > 0){
					for (int i = 0; i < dataArray.size(); i++) {
						JSONObject jo = dataArray.getJSONObject(i);
						synchronized (JDOrderController.class) {
							JdImportOrders.dao.crawling(jo);
						}
					}
					count ++;
					Thread.sleep(1 * 1000);
				}else {
					renderText("一共执行了"+conData+"数据。body:"+body);
					return;
				}
				if(StringUtils.isNotBlank(orderIdPara)){
					renderText("一共执行了"+conData+"数据。body:"+body);
					return;
				}
				}else {
					if(co >= 1){
						renderText("一共执行了"+conData+"数据。body:"+body);
						return;
					}
					co ++;
				}
			} catch (Exception e) {
				renderText("一共执行了"+conData+"数据。");
				return;
			}
		}
	}
		
	
}
