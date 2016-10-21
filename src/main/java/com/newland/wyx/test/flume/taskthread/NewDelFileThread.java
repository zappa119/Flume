package com.newland.wyx.test.flume.taskthread;

import java.io.File;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;








import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.logreader.bean.LogFileInfo;
import com.newland.wyx.test.flume.logreader.reader.ILogReader;
import com.newland.wyx.test.flume.logreader.reader.LogReaderPool;
import com.newland.wyx.test.flume.logreader.tools.LogFileLineCountParser;
import com.newland.wyx.test.flume.logreader.tools.MarkFileParser;

/**
 * 新的删除业务需求
 * @author 吴越骁
 *
 */
public class NewDelFileThread extends AbstractTaskThread {
	public Logger logger = LoggerFactory.getLogger(NewDelFileThread.class);
    
	/**
	 * 阅读池
	 */
	private LogReaderPool readerPool;
    
	/**
	 * 文件锁
	 */
	private Object fileSystemLock;
    
	/**
	 * 最大打开时间
	 */
	private long maxOpenTimeMs;
	
    /**
     *最大修改间隔 
     */
	private long maxIdleTimeMs;

   /**
    * 构建一个新的进程
    * @param newDelTaskIntervalMinute
    * @param taskInfo
    * @param readerPool
    * @param fileSystemLock
    * @param maxOpenTimeMinute
    * @param maxIdleTimeMinute
    */
	public NewDelFileThread(int newDelTaskIntervalMinute, String taskInfo,
			LogReaderPool readerPool, Object fileSystemLock,
			int maxOpenTimeMinute, int maxIdleTimeMinute) {
		super(newDelTaskIntervalMinute, taskInfo);
		this.readerPool = readerPool;
		this.fileSystemLock = fileSystemLock;
		maxOpenTimeMs = maxOpenTimeMinute * 60 * 1000;
		maxIdleTimeMs = maxIdleTimeMinute * 60 * 1000;
	}
    
	/**
	 * 新的删除业务逻辑:
	 * 1、获取当前系统时间
	 * 2、获取文件打开时间，系统当前时间与打开时间的差值如果大于设置的最大打开时间，就去检查文件的最大修改间隔时间
	 * 3、如果系统当前时间与最后一次文件修改时间的差值大于最大修改间隔时间，就去检查此文件的读取进度
	 * 4、如果读取的行数与此文件的总行数一致，先从reader中释放此文件资源，直接搬迁日志到指定文件夹，并且删除此日志的mark文件，
	 */
	@Override
	protected void doTask() {
		long currentTime = System.currentTimeMillis();
		synchronized (fileSystemLock) {
			List<ILogReader> logReaderSnapshot = readerPool.getReaderSnapshot();
			List<ILogReader> needCheckReaders = new ArrayList<ILogReader>();
			List<ILogReader> needRemoveReaders = new ArrayList<ILogReader>();
			List<LogFileInfo> needDelFiles = new ArrayList<LogFileInfo>();
			List<File> needDelMetaFiles = new ArrayList<File>();
			for (ILogReader reader : logReaderSnapshot) {
				if ((currentTime - reader.getLogFileInfo().getOpenTime()) > maxOpenTimeMs) {
					needCheckReaders.add(reader);
				}
			}
			for (ILogReader reader : needCheckReaders) {
				File logFile = new File(reader.getLogFileInfo()
						.getFileFullPath());
				LogFileInfo logFileInfo = reader.getLogFileInfo();
				long lastModifyTime = logFile.lastModified();
				boolean del = false;
				if (lastModifyTime == 0L) {
					del = true;
				} else if ((currentTime - lastModifyTime) > maxIdleTimeMs) {
					del = true;
				}
				if (del) {
					File markFile = new File(reader.getLogFileInfo()
							.getMarkFullPath());
					long markLine = -1;
					long logLine = -1;
					try {
						markLine = MarkFileParser.parseLine(markFile);
						logLine = LogFileLineCountParser
								.getLogFileLineCount(logFile);

					} catch (IOException e) {
						logger.error("---[log file progress check failed]"
								+ logFile.getPath());
						continue;
					}
					if (markLine == logLine) {
						needRemoveReaders.add(reader);
						needDelFiles.add(logFileInfo);
						needDelMetaFiles.add(markFile);
						logger.info("---[prepare bak]" + logFile.getPath()
								+ "(line:" + logLine + ")");
					}
				}
			}
			readerPool.removeReader(needRemoveReaders);
			for (ILogReader logReader : needRemoveReaders) {
				logReader.releaseIfNeed();
			}
			try {
				for (LogFileInfo logFileInfo : needDelFiles) {
						doBak(logFileInfo);
				}
			} catch (Exception e) {
				logger.error("---" + getTaskInfo() + "field.", e);
			}
			
			for (File markFile : needDelMetaFiles) {
				boolean delSuccess = markFile.delete();
				if (!delSuccess) {
					logger.warn("---[remove mark file failed]"
							+ markFile.getPath());
				}
			}
		}
	}
	
	private void doBak(LogFileInfo logFileInfo) {
		String filePath = logFileInfo.getFilePath() + logFileInfo.getFileName();
		String bakPath = logFileInfo.getBakPath() + logFileInfo.getFileName();
		if (filePath.equals(bakPath)) {
			return;
		}
		File bakFolder = new File(logFileInfo.getBakPath());
		if (!bakFolder.exists()) {
			bakFolder.mkdirs();
		}
		new File(filePath).renameTo(new File(bakPath));
		logger.info("--------bak file:"+filePath+"success");
	}

}
