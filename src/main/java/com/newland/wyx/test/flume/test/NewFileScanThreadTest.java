package com.newland.wyx.test.flume.test;

import java.util.List;

import com.google.common.collect.Lists;
import com.newland.wyx.test.flume.logreader.bean.FolderScanInfo;
import com.newland.wyx.test.flume.logreader.reader.LogReaderPool;
import com.newland.wyx.test.flume.taskthread.NewFileScanThread;

public class NewFileScanThreadTest {
	private static final String FILE_PATH = "E:\\test";
	private static final String MARK_PATH = "E:\\test";
	
	public static void main(String[] args) {
		LogReaderPool readPool = new LogReaderPool();
		List<FolderScanInfo> list = Lists.newArrayList();
		//list.add(new FolderScanInfo(FILE_PATH,MARK_PATH));
		
		NewFileScanThread test = new NewFileScanThread(1, "test",readPool,list,new Object());
		
		new Thread(test).start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		
		
	}
}
