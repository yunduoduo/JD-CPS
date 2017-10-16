package com.raincc.task;

import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.log.Logger;
import com.raincc.robot.jd.core.jdutils.TUtils;
public class JdUpAgoOrderTask implements Job {
	
	private static final Logger _log = Logger.getLogger(JdUpAgoOrderTask.class);

	/* (non-Javadoc)
	 * 当前时间前60天到前30天的更新
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 执行时间：
	 * 		api接口：1：00、11：00、17：00
	 * 		京东接口：2：30、11：30、19：30
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		int houses = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		_log.info("JdUpAgoOrderTask:当前时间为："+houses+"时，每半小时执行一次。");
		if(minute == 0 && (houses == 1 || houses == 11 || houses == 17)){
			List<String> dateList = TUtils.getAfterDateList(-61,-30,1);//前一月
			for (String string : dateList) {
				TUtils.impOrder(string.replace("-", ""));
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else if(minute == 30 && (houses == 2 || houses == 11 || houses == 17)){
			List<String> dList = TUtils.getAfterDateList(-61,-30,1);//前一个月
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
