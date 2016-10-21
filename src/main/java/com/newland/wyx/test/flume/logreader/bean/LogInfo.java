package com.newland.wyx.test.flume.logreader.bean;

/**
 * 日志文本信息
 * @author 吴越骁
 */

public class LogInfo {
	/**
	 *  一行日志信息
	 */
	private String log;

	/**
	 *  文件全路径 
	 */
	private String fileFullPath;

	public LogInfo(String log, String fileFullPath) {
		this.log = log;
		this.fileFullPath = fileFullPath;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getFileFullPath() {
		return fileFullPath;
	}

	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}
}