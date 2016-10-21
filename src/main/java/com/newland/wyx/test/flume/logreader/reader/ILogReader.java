package com.newland.wyx.test.flume.logreader.reader;


import com.newland.wyx.test.flume.logreader.bean.LogFileInfo;

/**
 * 日志文件读取接口
 * @author 吴越骁
 *
 */
public interface ILogReader {
	/**
	 * 重置读取器
	 */
	public void reset();
	
	/**
	 * 每行日志文本
	 * @return
	 */
	public String readLine();
	
	/**
	 * 读取器状态
	 * @return
	 */
	public ReaderStatus status();
	
	/**
	 * 日志文件信息
	 * @return
	 */
	public LogFileInfo getLogFileInfo();
	
	/**
	 * mark方法
	 */
	public void mark();
	
	/**
	 * 释放reader资源
	 */
	public void releaseIfNeed();
	
       
}
