package com.newland.wyx.test.flume.taskthread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.logreader.bean.FolderScanInfo;
import com.newland.wyx.test.flume.logreader.bean.LogFileInfo;
import com.newland.wyx.test.flume.logreader.reader.ILogReader;
import com.newland.wyx.test.flume.logreader.reader.LogReader;
import com.newland.wyx.test.flume.logreader.reader.LogReaderPool;

/**
 * 新文件扫描线程
 * 
 * @author 吴越骁
 *
 */
public class NewFileScanThread extends AbstractTaskThread {

	public Logger logger = LoggerFactory.getLogger(NewFileScanThread.class);

	/**
	 * 读取管理容器
	 */
	private LogReaderPool readPool;

	/**
	 * 目标文件夹集合
	 */
	@SuppressWarnings("unused")
	private List<FolderScanInfo> folderScanInfoList;

	/**
	 * 线程锁
	 */
	private Object fileSystemLock;

	/**
	 * 日志扫描集合
	 */
	private List<FileScanner> fileScanners;

	public NewFileScanThread(long interval, String taskInfo,
			LogReaderPool readPool, List<FolderScanInfo> folderScanInfoList,
			Object fileSystemLock) {
		super(interval, taskInfo);
		this.readPool = readPool;
		this.folderScanInfoList = folderScanInfoList;
		this.fileSystemLock = fileSystemLock;
		fileScanners = new ArrayList<FileScanner>(folderScanInfoList.size());
		for (FolderScanInfo folderScanInfo : folderScanInfoList) {
			FileScanner fileScanner = new FileScanner(folderScanInfo);
			fileScanners.add(fileScanner);
		}
	}

	/**
	 * 线程执行方法 有线程锁,确保不会在扫描,标记,搬迁,删除等线程发生并行
	 */
	@Override
	protected void doTask() {
		synchronized (fileSystemLock) {
			try {
				for (FileScanner fileScanner : fileScanners) {
					List<LogFileInfo> list = fileScanner.scan();
					//对扫描到的所有日志,判断读取管理器中有没有这个文件
					//如果没有,生成读取器并且添加到读取管理容器中
					for (LogFileInfo logFileInfo : list) {
						if (!readPool.hasThisReader(logFileInfo)) {
							ILogReader reader = new LogReader(logFileInfo);
							reader.reset();
							readPool.addReader(reader);
						}
					}
				}

			} catch (Exception e) {
				logger.error("---" + getTaskInfo() + "field.", e);
			}

		}
	}

}
