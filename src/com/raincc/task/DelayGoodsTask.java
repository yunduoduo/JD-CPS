package com.raincc.task;

import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.raincc.robot.entity.DelaySendGoods;
public class DelayGoodsTask implements Job {
	
	private static final Logger _log = Logger.getLogger(DelayGoodsTask.class);
	
	/**
	 * 设置商品延迟推送有效
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Integer count = 0;
		List<DelaySendGoods> list = DelaySendGoods.dao.find("select delayGoodsId from rb_delay_send_goods where NOW() >= DelayTime and isValid = false "); 
		for (DelaySendGoods delay : list) {
			Db.update("update rb_delay_send_goods set isValid = true where delayGoodsId = ? ",delay.getInt("delayGoodsId"));
			count ++;
		}
		_log.info("商品推送设置了"+count+"个有效。");
	}

}
