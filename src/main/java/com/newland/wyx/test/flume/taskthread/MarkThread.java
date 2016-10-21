package com.newland.wyx.test.flume.taskthread;


import com.newland.wyx.test.flume.logreader.conf.ILogReadPlatform;

/**
 * Mark线程实现
 * @author 吴越骁
 *
 */
public class MarkThread extends AbstractTaskThread {
	private ILogReadPlatform logReadPlatform;

	public MarkThread(long markTaskIntervalSecond, String taskInfo,
			ILogReadPlatform logReadPlatform) {
		super(markTaskIntervalSecond, taskInfo);
		this.logReadPlatform = logReadPlatform;
	}

	@Override
	protected void doTask() {
         logReadPlatform.mark();
	}

}
