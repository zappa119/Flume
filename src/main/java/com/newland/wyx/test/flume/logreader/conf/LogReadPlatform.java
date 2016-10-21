package com.newland.wyx.test.flume.logreader.conf;

import java.util.ArrayList; 
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.logreader.bean.LogFileInfo;
import com.newland.wyx.test.flume.logreader.bean.LogInfo;
import com.newland.wyx.test.flume.logreader.reader.ILogReader;
import com.newland.wyx.test.flume.logreader.reader.LogReaderPool;
import com.newland.wyx.test.flume.taskthread.MarkThread;

/**
 * 超类
 * @author 吴越骁
 *
 */
public class LogReadPlatform implements ILogReadPlatform {
	Logger logger = LoggerFactory.getLogger(LogReadPlatform.class);
    
	/**
	 * 文件读取池,新增文件都会加入到pool
	 */
	protected LogReaderPool readerPool;
    
	/**
	 * 服务名
	 */
	protected String serviceName;
	
	/**
	 * mark线程
	 */
	protected MarkThread markThread;
    
	/**
	 * 初始化
	 */
	public void init(LogReadPlatformConf conf, String serviceName) {
		this.serviceName = serviceName;
		readerPool = new LogReaderPool();
		/**
		 * 如果有mark配置,则构造markThread
		 */
		int markInterval = conf.getMarkTaskIntervalSecond();
		if (markInterval > 0) {
			markThread = new MarkThread(
					markInterval,
					serviceName + "'s markThread",
					this
					);
		}
	}

	public void start() {
		if (markThread != null) {
			new Thread(markThread).start();
		}
	}

	public void stop() {
		// TODO Auto-generated method stub

	}
    
	/**
	 * 读取方法实现,readerpool执行nextRead方法
	 * 对每个文件循环读取
	 */
	public List<LogInfo> read() {
		List<LogInfo> logs = new ArrayList<LogInfo>();
		ILogReader reader = readerPool.nextRead();
		if (reader != null) {
			String line = reader.readLine();
			LogFileInfo logFileInfo = reader.getLogFileInfo();
			if (line != null) {
				logs.add(new LogInfo(line, logFileInfo.getFileFullPath()));
			}
		}
		return logs;
	}

	public long getReadLineCount() {
		return 0;
	}
    
	/**
	 * mark方法
	 */
	public void mark() {
        readerPool.markAll(); 		
	}

}
