package com.raincc.task;

import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.log.Logger;
import com.raincc.robot.jd.core.jdutils.TUtils;
public class JdUpdateOrderTask implements Job {
	
	private static final Logger _log = Logger.getLogger(JdUpdateOrderTask.class);
	
	/* (non-Javadoc)
	 * 当前时间前30天更新
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 执行时间：
	 * 		API接口：6：00、09：00、12：00、15：00、18：00、21：00
	 * 		京东接口：7：00、10：00、13：00、16：00、19：00、22：00
	 * 		
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		int houses = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		_log.info("JdUpdateOrderTask:当前时间为："+houses+"时，每半小时执行一次。");
		if(houses >= 5 && houses <= 23 && minute == 0 && houses % 3 == 0){
			List<String> dateList = TUtils.getAfterDateList(-31,0,1);//前一月
			for (String string : dateList) {
				TUtils.impOrder(string.replace("-", ""));
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else if(houses >= 5 && houses <= 23  &&  houses % 3 == 1){
			List<String> dList = TUtils.getAfterDateList(-31,0,1);//前一个月
			for (String time : dList) {
				TUtils.crawling(time);
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
