package com.raincc.robot.entity;

import java.util.Date;

import net.sf.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

@SuppressWarnings("serial")
@TableBind(tableName = "rb_jd_importorder_skus", pkName = "skusId")
public class JdImportOrderSkus extends BaseModel<JdImportOrderSkus> {

	public static final JdImportOrderSkus dao = new JdImportOrderSkus();

	@Before(Tx.class)
	public void saveSkus(JSONObject skusProduct,
			Integer importOrderId,Long orderId) {
		JdImportOrderSkus order =  new JdImportOrderSkus().findFirst("select skusId from rb_jd_importorder_skus where importOrderId = ? and skuId = ? ",importOrderId,skusProduct.getLong("skuId"));
		if(order == null){
			order = new JdImportOrderSkus();
			order.set("careteDate", new Date());
		}
		order.set("commission",skusProduct.getDouble("commission"));
		order.set("orderId",orderId);
		order.set("commissionRate",skusProduct.getDouble("commissionRate"));
		order.set("cosPrice",skusProduct.getDouble("cosPrice"));
		order.set("firstLevel",skusProduct.getInt("firstLevel"));
		order.set("price",skusProduct.getDouble("price"));
		order.set("secondLevel",skusProduct.getInt("secondLevel"));
		order.set("skuId",skusProduct.getLong("skuId"));
		order.set("skuName",skusProduct.getString("skuName"));
		order.set("skuNum",skusProduct.getInt("skuNum"));
		order.set("skuReturnNum",skusProduct.getInt("skuReturnNum"));
		order.set("subSideRate",skusProduct.getDouble("subSideRate"));
		order.set("subsidyRate",skusProduct.getDouble("subsidyRate"));
		order.set("thirdLevel",skusProduct.getInt("thirdLevel"));
		order.set("valid",skusProduct.getInt("valid"));
		order.set("wareId",skusProduct.getLong("wareId"));
		order.set("importOrderId",importOrderId);
//		System.err.println(skusProduct.toString());
		if(skusProduct.toString().contains("subUnionId")){
			Long subUnionId = skusProduct.getLong("subUnionId");
			if(subUnionId != null && subUnionId > 0){
				order.set("subUnionId", subUnionId);
				Db.update("update rb_jd_importorders set channelUnionId = ? where id = ? ",subUnionId,importOrderId);
			}
		}
		order.saveOrUpdate();
	}

	/**爬取京东商品更新
	 * @param skusProduct
	 * @param id
	 */
	@Before(Tx.class)
	public void crawlingSkus(JSONObject p, Integer id,Long orderId) {
		Long skuId = p.getLong("skuId");
		JdImportOrderSkus order =  new JdImportOrderSkus().findFirst("select skusId from rb_jd_importorder_skus where importOrderId = ? and skuId = ? ",id,skuId);
		if(order == null){
			order = new JdImportOrderSkus();
			order.set("careteDate", new Date());
		}
		order.set("orderId",orderId);
		order.set("skuId",skuId);
		order.set("importOrderId",id);
		order.set("skuName",p.getString("skuName"));
		order.set("skuNum",p.getInt("skuNum"));
		order.set("skuReturnNum",p.getInt("skuReturnNum"));
		order.set("skuPrice",p.getDouble("skuPrice"));
		order.set("commissionRate",p.getDouble("commissionRate"));
		order.set("subsideProportion",p.getDouble("subsideProportion"));
		order.set("commission",p.getDouble("commission"));
		order.set("ruleType",p.getInt("ruleType"));
		order.set("balance",p.getInt("balance"));
		order.saveOrUpdate();
	}

}
