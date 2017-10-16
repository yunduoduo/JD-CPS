package com.raincc.robot.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.raincc.robot.jd.core.jdutils.TUtils;

@SuppressWarnings("serial")
@TableBind(tableName = "rb_jd_importorders", pkName = "id")
public class JdImportOrders extends BaseModel<JdImportOrders> {

	public static final JdImportOrders dao = new JdImportOrders();

	@Before(Tx.class)
	public Integer saveOrder(final JSONObject j,final Long unionId) {
		synchronized (JdImportOrders.class) {
			Date date = new Date();
			Integer id = null;
			JdImportOrders order = JdImportOrders.dao.findFirst("select id from rb_jd_importorders where orderId = ? and balance = 1 and subUnionId = ? ",j.getLong("orderId"),unionId);
			Boolean bo = false;
			if(order == null){//已经结算的订单不处理
				JdImportOrders jd = new JdImportOrders().findFirst("select id from rb_jd_importorders where orderId = ? and balance = 2 and subUnionId = ? ",j.getLong("orderId"),unionId);
				if(jd == null){
					bo = true;
					jd = new JdImportOrders();
					jd.set("createDate",date);
				}
				jd.set("subUnionId",unionId);
				jd.set("balance",j.getInt("balance"));
				jd.set("commission",j.getDouble("commission"));
				jd.set("cosPrice", j.getDouble("cosPrice"));
				Long finishTime = j.getLong("finishTime");
				if(finishTime != null && finishTime > 0){
					jd.set("finishTime",TUtils.getLongToDate(finishTime));
				}
				jd.set("orderId", j.getLong("orderId"));
				Long sLong = j.getLong("orderTime");
				if(sLong != null && sLong > 0){
					jd.set("orderTime", TUtils.getLongToDate(sLong));
				}
				jd.set("parentId", j.getLong("parentId"));
				jd.set("plus", j.getInt("plus"));
				jd.set("popId", j.getInt("popId"));
				jd.set("sourceEmt", j.getInt("sourceEmt"));
				jd.set("yn", j.getInt("yn"));
				jd.set("splitType", j.getInt("splitType"));
				jd.set("totalMoney", j.getDouble("totalMoney"));
				jd.set("upTime", date);
				jd.saveOrUpdate();
				id = jd.getInt("id");
			}
			if(bo && id != null){
				//存储订单中的商品
				JSONArray skus = j.getJSONArray("skus");
				for (int k = 0; k < skus.size(); k++) {
					JSONObject skusProduct = skus.getJSONObject(k);
					JdImportOrderSkus.dao.saveSkus(skusProduct,id,j.getLong("orderId"));
				}
			}
			return id;
		}
	}

	/**爬取的京东订单
	 * @param jo
	 */
	@Before(Tx.class)
	public void crawling(final JSONObject jo) {
		synchronized (JdImportOrders.class) {
			Long orderId = jo.getLong("orderId");
			Long unionId = jo.getLong("unionId");
			String balanceName = "已结算";
			JdImportOrders od = new JdImportOrders().findFirst("select id from rb_jd_importorders where subUnionId = ? and orderId = ? and balanceName= ? ",unionId,orderId,balanceName);
			if(od == null){
				JdImportOrders orders = new JdImportOrders().findFirst("select id from rb_jd_importorders where subUnionId = ? and orderId = ? ",unionId,orderId);
				if(orders == null){
//				orders = new JdImportOrders();
//				orders.set("createDate",new Date());
					return;
				}
				orders.set("orderId",orderId);
				orders.set("subUnionId",unionId);
				orders.set("orderTime",jo.get("orderDate"));
				String finishDate = jo.getString("finishDate");
				if(StringUtils.isNotBlank(finishDate) && !finishDate.contains("--")){
					orders.set("finishTime",finishDate);
				}
				String balance = jo.getString("balance");
				if(StringUtils.isNotBlank(balance) && !"null".equals(balance)){
					orders.set("balance",Integer.parseInt(balance));
				}
				orders.set("commission",jo.getDouble("commission"));
				orders.set("balanceName",jo.getString("balanceName"));
				orders.set("yn",jo.getInt("yn"));
				orders.set("sourceEmt",jo.getInt("sourceEmt"));
				orders.set("plus",jo.getInt("plus"));
				orders.set("spName",jo.getString("spName"));
				orders.set("splitName",jo.getString("splitName"));
				orders.set("orderStatusName",jo.getString("orderStatusName"));
				orders.set("parentId",jo.getLong("parentId"));
				orders.set("cosPrice",jo.getDouble("cosPrice"));
				orders.set("upTime",new Date());
				orders.saveOrUpdate();
				Integer id = orders.getInt("id");
				JSONArray skus = jo.getJSONArray("skus");
				for (int k = 0; k < skus.size(); k++) {
					JSONObject skusProduct = skus.getJSONObject(k);
					JdImportOrderSkus.dao.crawlingSkus(skusProduct,id,orderId);
				}
			}
		}
		
	}
	
	/**个人爬取的京东订单
	 * @param jo
	 */
	@Before(Tx.class)
	public void individual(final JSONObject jo) {
		synchronized (JdImportOrders.class) {
			Long orderId = jo.getLong("orderId");
			Long unionId = jo.getLong("unionId");
			String balanceName = "已结算";
			JdImportOrders od = new JdImportOrders().findFirst("select id from rb_jd_importorders where subUnionId = ? and orderId = ? and balanceName= ? ",unionId,orderId,balanceName);
			if(od == null){
				JdImportOrders orders = new JdImportOrders().findFirst("select id from rb_jd_importorders where subUnionId = ? and orderId = ? ",unionId,orderId);
				if(orders == null){
					orders = new JdImportOrders();
					orders.set("createDate",new Date());
				}
				orders.set("orderId",orderId);
				orders.set("subUnionId",unionId);
				orders.set("orderTime",jo.get("orderDate"));
				String finishDate = jo.getString("finishDate");
				if(StringUtils.isNotBlank(finishDate) && !finishDate.contains("--")){
					orders.set("finishTime",finishDate);
				}
				String balance = jo.getString("balance");
				if(StringUtils.isNotBlank(balance) && !"null".equals(balance)){
					orders.set("balance",Integer.parseInt(balance));
				}
				orders.set("commission",jo.getDouble("commission"));
				orders.set("balanceName",jo.getString("balanceName"));
				orders.set("yn",jo.getInt("yn"));
				orders.set("sourceEmt",jo.getInt("sourceEmt"));
				orders.set("plus",jo.getInt("plus"));
				orders.set("spName",jo.getString("spName"));
				orders.set("splitName",jo.getString("splitName"));
				orders.set("orderStatusName",jo.getString("orderStatusName"));
				orders.set("parentId",jo.getLong("parentId"));
				orders.set("cosPrice",jo.getDouble("cosPrice"));
				orders.set("upTime",new Date());
				orders.saveOrUpdate();
				Integer id = orders.getInt("id");
				JSONArray skus = jo.getJSONArray("skus");
				for (int k = 0; k < skus.size(); k++) {
					JSONObject skusProduct = skus.getJSONObject(k);
					JdImportOrderSkus.dao.crawlingSkus(skusProduct,id,orderId);
				}
			}
		}
		
	}

}
