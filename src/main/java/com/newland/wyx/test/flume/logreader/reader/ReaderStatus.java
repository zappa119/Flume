package com.newland.wyx.test.flume.logreader.reader;

/**
 * 读取器状态
 * @author 吴越骁
 *
 */
public enum ReaderStatus {
	/**
	 * 关闭
	 */
	CLOSE,

	/**
	 * 正常
	 */
	NORMAL,

	/**
	 * 读取正常，无新数据
	 */
	NORMAL_NODATA,

	/**
	 * 读取正常，mark失败
	 */
	NORMAL_NOMARK,

	/**
	 * 状态异常
	 */
	EXCEPTION,

	/**
	 * 暂无文件
	 */
	NOFILE;
}
