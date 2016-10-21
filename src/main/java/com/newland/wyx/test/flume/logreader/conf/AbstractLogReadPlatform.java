package com.newland.wyx.test.flume.logreader.conf;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.taskthread.DelFileThread;
import com.newland.wyx.test.flume.taskthread.NewDelFileThread;
import com.newland.wyx.test.flume.taskthread.NewFileScanThread;

/**
 * abstract策略readplatform
 * 
 * @author 吴越骁
 *
 */
public class AbstractLogReadPlatform extends LogReadPlatform {

	public Logger logger = LoggerFactory
			.getLogger(AbstractLogReadPlatform.class);
	/**
	 * 新文件扫描线程
	 */
	private NewFileScanThread newFileScanThread;

	/**
	 * 线程锁,防止scan,mark,del等线程并行
	 */
	private Object fileSystemLock;
    
	/**
	 * 删除日志文件线程
	 */
	private DelFileThread delFileThread;
	
	/**
	 * 新的日志删除线程
	 */
	private NewDelFileThread newDelFileThread;

	/**
	 * 初始化线程信息
	 */
	@Override
	public void init(LogReadPlatformConf conf, String serviceName) {
		super.init(conf, serviceName);
		fileSystemLock = new Object();
		int scanNewFileTaskIntervalMinute = conf
				.getScanNewFileTaskIntervalMinute();
		/**
		 * 新文件扫描线程的初始化
		 */
		if (scanNewFileTaskIntervalMinute > 0) {
			newFileScanThread = new NewFileScanThread(
					scanNewFileTaskIntervalMinute, serviceName, readerPool,
					conf.getScanList(), fileSystemLock);
		}
		/**
		 * 删除线程的初始化
		 */
		int delTaskIntervalMinute = conf.getDelTaskIntervalSecond();
		if (delTaskIntervalMinute > 0) {
			delFileThread = new DelFileThread(delTaskIntervalMinute,
					serviceName + "'s delFileThread", readerPool,
					fileSystemLock, conf.getMaxOpenTimeMinute(),
					conf.getMaxIdleTimeMinute());
		}
		/**
		 * 新的删除线程初始化
		 */
		int newDelTaskIntervalMinute = conf.getNewDelTaskIntervalMinute();
		if (newDelTaskIntervalMinute > 0) {
			newDelFileThread = new NewDelFileThread(newDelTaskIntervalMinute,
					serviceName + "'s newDelFileThread", readerPool,
					fileSystemLock, conf.getMaxOpenTimeMinute(),
					conf.getMaxIdleTimeMinute());
		}
	}

	/**
	 * 开始各个线程
	 */
	@Override
	public void start() {
		if (newFileScanThread != null) {
			new Thread(newFileScanThread).start();
		}
		if (delFileThread != null) {
			new Thread(delFileThread).start();
		}
		if(newDelFileThread != null){
			new Thread(newDelFileThread).start();
		}
		super.start();
	}
}
