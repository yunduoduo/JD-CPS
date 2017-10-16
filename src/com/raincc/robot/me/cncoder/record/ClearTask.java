package com.raincc.robot.me.cncoder.record;
/*
 * 定时清除聊天缓存变量
 */


public class ClearTask implements Runnable{
	int pauseTime;
	public ClearTask(int p){
		pauseTime=p;
	}

	public void run() {
		while (true) {
			RecordCon.cache.clear();
			System.out.println("清除缓存成功！");
			try {
				Thread.sleep(pauseTime*60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
