package com.newland.wyx.test.flume.logreader.bean;

public class FolderScanInfo {
	/** 
	 *目标所在文件夹 
	 */
	private String logFolder;
	
	/**
	 *mark所在文件夹
	 */
	private String markFolder;
	
	private String bakFolder;

	public FolderScanInfo(String logFolder,String markFolder,String bakFolder) {
		this.logFolder = logFolder;
		this.markFolder = markFolder;
		this.bakFolder = bakFolder;
	}

	public String getLogFolder() {
		return logFolder;
	}
	
	public String getMarkFolder() {
		return markFolder;
	}

	public String getBakFolder() {
		return bakFolder;
	}

	public void setBakFolder(String bakFolder) {
		this.bakFolder = bakFolder;
	}

	
	
}
