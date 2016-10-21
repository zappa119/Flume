package com.newland.wyx.test.flume.taskthread;

import com.newland.wyx.test.flume.logreader.reader.LogReaderPool;

public class BakFileThread extends AbstractTaskThread{
    private LogReaderPool readerPool;
    
    private Object fileSystemLock;
	
	
	public BakFileThread(long interval, String taskInfo,LogReaderPool readerPool, 
			Object fileSystemLock) {
		super(interval, taskInfo);
		this.readerPool = readerPool;
		this.fileSystemLock = fileSystemLock;
	}

	@Override
	protected void doTask() {
		// TODO Auto-generated method stub
		
	}

}
