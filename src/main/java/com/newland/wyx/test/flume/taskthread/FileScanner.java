package com.newland.wyx.test.flume.taskthread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.logreader.bean.FolderScanInfo;
import com.newland.wyx.test.flume.logreader.bean.LogFileInfo;

/**
 * 日志扫描器
 * @author 吴越骁
 *
 */
public class FileScanner {
    
	public Logger logger = LoggerFactory.getLogger(FileScanner.class);
	
	/**
	 * 目标文件夹信息
	 */
	private FolderScanInfo folderScanInfo;
	
	/**
	 * 文件夹过滤器
	 */
	private LogFileFilter logFileFilter = new LogFileFilter();

	public FileScanner(FolderScanInfo folderScanInfo) {
		this.folderScanInfo = folderScanInfo;
	}
    
	/**
	 * 扫描方法
	 * 如果是目标是文件夹,获取到里面的每个日志的信息,返回日志集合。
	 * @return 日志文件集合
	 */
	public List<LogFileInfo> scan() {
		String folderPath = folderScanInfo.getLogFolder();
		File folder = new File(folderPath);
		if (folder.isDirectory()) {
			File [] logFile = folder.listFiles(logFileFilter);
			List<LogFileInfo> logFileInfoList = new ArrayList<LogFileInfo>(logFile.length);
			for (File file : logFile) {
				LogFileInfo logFileInfo = new LogFileInfo(
						folderPath, 
						file.getName(),
						folderScanInfo.getMarkFolder(),
						folderScanInfo.getBakFolder()
						);
				logFileInfoList.add(logFileInfo);
			}
			return logFileInfoList;
		} else {
			throw new IllegalArgumentException("scan path is not a folder.(" + folderPath + ")");
		}
	}

}
