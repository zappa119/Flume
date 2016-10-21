package com.newland.wyx.test.flume.logreader.bean;

/**
 * 日志文件信息
 * 
 * @author 吴越骁
 *
 */

public class LogFileInfo {
	/**
	 * 文件路径
	 */
	private String filePath;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件全路径,格式为文件路径+文件名
	 */
	private String fileFullPath;
	
	/**
	 * mark路径
	 */
	private String markPath;
	
    /**
     * mark全路径 
     */
	private String markFullPath;
	
	/**
	 * 文件打开时间,在构造器对象时为系统当前时间
	 */
	private long openTime;
	
	/**
	 * 搬迁路径
	 */
	private String bakPath;

	public LogFileInfo(String filePath, String fileName, String markPath,String bakPath) {
		this.filePath = filePath;
		this.fileName = fileName;
		this.markPath = markPath;
		fileFullPath = filePath + fileName;
		markFullPath = markPath + fileName + ".meta";
		openTime = System.currentTimeMillis();
		this.bakPath = bakPath;
	}
	

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileFullPath() {
		return fileFullPath;
	}

	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}

	public String getMarkPath() {
		return markPath;
	}

	public String getMarkFullPath() {
		return markFullPath;
	}


	public long getOpenTime() {
		return openTime;
	}


	public void setOpenTime(long openTime) {
		this.openTime = openTime;
	}


	public String getBakPath() {
		return bakPath;
	}


	public void setBakPath(String bakPath) {
		this.bakPath = bakPath;
	}

}
