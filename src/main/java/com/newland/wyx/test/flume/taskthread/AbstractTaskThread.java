package com.newland.wyx.test.flume.taskthread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程抽象类
 * @author 吴越骁
 *
 */
public abstract class AbstractTaskThread implements Runnable {
	private Logger logger = LoggerFactory.getLogger(AbstractTaskThread.class);
    
	/**
	 * 线程执行状态
	 */
	private boolean running = true;
    
	/**
     *执行间隔 
     */
	private long interval;
    
	/**
	 * 线程信息
	 */
	private String taskInfo;
	
	public AbstractTaskThread(long interval, String taskInfo) {
		this.interval = interval;
		this.taskInfo = taskInfo;
	}

	public void run() {
		while (running) {
			doTask();
			logger.info("---" + taskInfo + " run.");
			try {
				Thread.sleep(interval * 5 * 1000);
			} catch (InterruptedException e) {
			}
		}

	}

	protected abstract void doTask();
	
	public void stop() {
		running = false;
	}
	
	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}
	
	public String getTaskInfo() {
		return taskInfo;
	}
	
	public void setInterval(long interval) {
		this.interval = interval;
	}

}
