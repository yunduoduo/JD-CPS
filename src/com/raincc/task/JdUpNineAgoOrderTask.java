package com.raincc.task;

import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.log.Logger;
import com.raincc.robot.jd.core.jdutils.TUtils;
public class JdUpNineAgoOrderTask implements Job {
	
	private static final Logger _log = Logger.getLogger(JdUpNineAgoOrderTask.class);

	/* (non-Javadoc)
	 * 当前时间前90天到前60天的更新
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 执行时间：
	 * 		API：14：00、23：00
	 * 		京东：14：30、00：30
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		int houses = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		_log.info("JdUpNineAgoOrderTask:当前时间为："+houses+"时，每半小时执行一次。");

		if(minute == 0 && (houses == 14 || houses == 23)){
			List<String> dateList = TUtils.getAfterDateList(-90,-60,1);//前一月
			for (String string : dateList) {
				TUtils.impOrder(string.replace("-", ""));
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else if(minute == 30 && (houses == 14 || houses == 0)){
			List<String> dList = TUtils.getAfterDateList(-90,-60,1);//前一个月
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
	/* (non-Javadoc)
	 * 当前时间前30天更新
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 执行时间：
	 * 		API接口： 6：00、09：00、12：00、15：00、18：00、21：00
	 * 		京东接口：7：00、10：00、13：00、16：00、19：00、22：00
	 * 当前时间前60天到前30天的更新
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 执行时间：
	 * 		api接口： 1：00、11：00、17：00
	 * 		京东接口：2：30、11：30、17：30
	 * 当前时间前90天到前60天的更新
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 执行时间：
	 * 		API： 14：00、23：00
	 * 		京东：14：30、00：30
	 *
	 * 1：30、2：00、3：00、3：30、4：00、4：30、5：00、5：30
	 * 6：30、7：30、8：00、8：30、9：30、10：30、12：30、13：30
	 * 15：30、16：30、18：30、20：00、20：30、21：30
	 * 22：30、23：30、0：00
	 */

}
