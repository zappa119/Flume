package com.newland.wyx.test.flume.logreader.bean;

/**
 * 日志读取进度信息
 * 
 * @author 吴越骁
 *
 */
public class MarkInfo {
	/**
	 * 读取的行号
	 */
	private long lineNO;

	/**
	 * 总偏移量
	 */
	private long offset;

	/**
	 * 表明文件有新内容添加后是否需要mark
	 */
	private boolean needMark;

	/**
	 * 创建一个空的markInfo
	 */
	public MarkInfo() {
	}

	/**
	 * 获取旧的markInfo后 构造一个markInfo 格式为 行号:总偏移量
	 * 
	 * @param markStr
	 */
	public MarkInfo(String markStr) {
		if (markStr != null && !"".equals(markStr.trim())) {
			String[] markInfo = markStr.trim().split(":");
			if (markInfo.length == 2) {
				try {
					lineNO = Long.parseLong(markInfo[0]);
					offset = Long.parseLong(markInfo[1]);
				} catch (NumberFormatException e) {
					lineNO = 0;
					offset = 0;
				}
			}
		}
	}

	/**
	 * 重置markInfo
	 */
	public void reset() {
		lineNO = 0;
		offset = 0;
		needMark = true;
	}

	/**
	 * 读取一行数据后markInfo的记录方式
	 * 
	 * @param linelength
	 */
	public void addLine(int linelength) {
		lineNO++;
		offset += linelength + 1;
		needMark = true;
	}

	public long getLineNo() {
		return lineNO;
	}

	public long getOffset() {
		return offset;
	}

	public boolean needMark() {
		if (needMark) {
			needMark = false;
			return true;
		}
		return false;
	}

	public String getMarkStr() {
		return lineNO + ":" + offset;
	}

	@Override
	public String toString() {
		return "MarkInfo [lineNO=" + lineNO + ", offset=" + offset
				+ ", needMark=" + needMark + "]";
	}

}
