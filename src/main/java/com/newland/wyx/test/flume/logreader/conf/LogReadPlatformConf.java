package com.newland.wyx.test.flume.logreader.conf;

import java.util.List; 

import com.newland.wyx.test.flume.logreader.bean.FolderScanInfo;

/**
 * 读取平台配置类
 * @author 吴越骁
 *
 */
public class LogReadPlatformConf {
	/**
	 * 扫描目标文件夹集合
	 */
	private List<FolderScanInfo> scanList;
    
	/**
	 * 配置项:扫描线程间隔时间
	 */
	private int scanNewFileTaskIntervalMinute;
	
	/**
	 * 配置项:mark线程间隔时间
	 */
	private int markTaskIntervalSecond;
	
	/**
	 * 配置项:del线程间隔时间
	 */
    private int delTaskIntervalSecond;
    
    /**
	 * 配置项:文件最大打开时间
	 */
    private int maxOpenTimeMinute;
    
    /**
	 * 配置项:文件最大修改间隔时间
	 */
    private int maxIdleTimeMinute;
    
    /**
     * 配置项:新的扫描线程间隔时间
     */
    private int newDelTaskIntervalMinute;
    
	public LogReadPlatformConf(List<FolderScanInfo> scanList,
			int scanNewFileTaskIntervalMinute) {
		this.scanNewFileTaskIntervalMinute = scanNewFileTaskIntervalMinute;
		this.scanList = scanList;
	}

	public List<FolderScanInfo> getScanList() {
		return scanList;
	}

	public int getScanNewFileTaskIntervalMinute() {
		return scanNewFileTaskIntervalMinute;
	}

	public void setScanNewFileTaskIntervalMinute(
			int scanNewFileTaskIntervalMinute) {
		this.scanNewFileTaskIntervalMinute = scanNewFileTaskIntervalMinute;
	}

	public void setScanList(List<FolderScanInfo> scanList) {
		this.scanList = scanList;
	}

	public int getMarkTaskIntervalSecond() {
		return markTaskIntervalSecond;
	}

	public void setMarkTaskIntervalSecond(int markTaskIntervalSecond) {
		this.markTaskIntervalSecond = markTaskIntervalSecond;
	}

	public int getDelTaskIntervalSecond() {
		return delTaskIntervalSecond;
	}

	public void setDelTaskIntervalSecond(int delTaskIntervalSecond) {
		this.delTaskIntervalSecond = delTaskIntervalSecond;
	}

	public int getMaxOpenTimeMinute() {
		return maxOpenTimeMinute;
	}

	public void setMaxOpenTimeMinute(int maxOpenTimeMinute) {
		this.maxOpenTimeMinute = maxOpenTimeMinute;
	}

	public int getMaxIdleTimeMinute() {
		return maxIdleTimeMinute;
	}

	public void setMaxIdleTimeMinute(int maxIdleTimeMinute) {
		this.maxIdleTimeMinute = maxIdleTimeMinute;
	}

	public int getNewDelTaskIntervalMinute() {
		return newDelTaskIntervalMinute;
	}

	public void setNewDelTaskIntervalMinute(int newDelTaskIntervalMinute) {
		this.newDelTaskIntervalMinute = newDelTaskIntervalMinute;
	}

}
