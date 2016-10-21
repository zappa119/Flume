package com.newland.wyx.test.flume.logreader.conf;

import java.util.List;

import com.newland.wyx.test.flume.logreader.bean.LogInfo;


/**
 * 日志读取平台接口
 * 提供各种方法
 * @author 吴越骁
 *
 */
public interface ILogReadPlatform {
	/**
	 * 初始化平台
	 * @param conf
	 * @param serviceName
	 */
	public void init(LogReadPlatformConf conf, String serviceName);
	
	/**
	 * 启动,包括初始化文件流,启动各个线程
	 */
	public void start();
	
	/**
	 * 停止,终止各个线程
	 */
	public void stop();
	
	/**
	 * 读取日志方法
	 * @return
	 */
	public List<LogInfo> read();
	
	
	/**
	 * mark方法
	 */
	public void mark();
	
	public long getReadLineCount();
}
