package com.newland.wyx.test.flume.logreader.reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.logreader.bean.LogFileInfo;
import com.newland.wyx.test.flume.logreader.bean.MarkInfo;

/**
 * 读取器实现类
 * 
 * @author 吴越骁
 *
 */
public class LogReader implements ILogReader {
	public Logger logger = LoggerFactory.getLogger(LogReader.class);

	/**
	 * 读取器状态
	 */
	private ReaderStatus status;

	/**
	 * 日志文件信息
	 */
	private LogFileInfo logFileInfo;

	/**
	 * 读取器
	 */
	private LineNumberReader lineNumberReader;

	/**
	 * 线程锁
	 */
	private Object resetLock;

	/**
	 * mark信息
	 */
	private MarkInfo markInfo;

	/**
	 * 初始化读取器,状态为close
	 * 
	 * @param logFileInfo
	 */
	public LogReader(LogFileInfo logFileInfo) {
		this.logFileInfo = logFileInfo;
		resetLock = new Object();
		status = ReaderStatus.CLOSE;
	}

	/**
	 * 设置读取器,新建LineNumberReader对象及状态为NORMAL
	 */
	public void reset() {
		releaseIfNeed();
		// 先获取mark信息
		markInfo = getMarkInfo();
		synchronized (resetLock) {
			if (status != ReaderStatus.EXCEPTION
					&& status != ReaderStatus.NOFILE) {
				try {
					lineNumberReader = new LineNumberReader(
							new InputStreamReader(new FileInputStream(
									logFileInfo.getFileFullPath())));
					// 如果mark信息不为空并且偏移量>0,则跳过偏移量
					if (markInfo != null && markInfo.getOffset() > 0) {
						lineNumberReader.skip(markInfo.getOffset());
					}
					status = ReaderStatus.NORMAL;
				} catch (FileNotFoundException e) {
					status = ReaderStatus.NOFILE;
				} catch (IOException e) {
					status = ReaderStatus.EXCEPTION;
				}
			}
		}

	}

	/**
	 * 读取日志文件 状态为NORMAL或者NORMAL_NODATA、NORMAL_NOMARK可读取
	 * lineNumberReader.readline为增量读取 返回读取到的一行信息
	 */
	public String readLine() {
		if (status == ReaderStatus.NORMAL
				|| status == ReaderStatus.NORMAL_NODATA
				|| status == ReaderStatus.NORMAL_NOMARK) {
			String line = null;
			try {
				line = lineNumberReader.readLine();
			} catch (IOException e) {
				status = ReaderStatus.EXCEPTION;
			}
			if (line != null) {
				if (status == ReaderStatus.NORMAL_NODATA) {
					status = ReaderStatus.NORMAL;
				}
			} else {
				status = ReaderStatus.NORMAL_NODATA;
			}
			/**
			 * 方法内容新增 读取日志时同时标记mark信息
			 */
			if (line != null) {
				if (markInfo != null) {
					markInfo.addLine(line.length());
				}
				if (status == ReaderStatus.NORMAL_NODATA) {
					status = ReaderStatus.NORMAL;
				}
			} else {
				status = ReaderStatus.NORMAL_NODATA;
			}
			return line;
		}
		return null;
	}

	public ReaderStatus status() {
		// TODO Auto-generated method stub
		return status;
	}

	public LogFileInfo getLogFileInfo() {
		return logFileInfo;
	}

	/**
	 * mark方法实现 实现逻辑: 首先判断是否需要mark,如果需要,进入方法 获取到mark的全路径后,写入markInfo
	 */
	public void mark() {
		if (markInfo.needMark()) {
			BufferedWriter markWriter = null;
			File markFolder = new File(logFileInfo.getMarkPath());
			logger.info("##################markFolder:"+markFolder.getPath()+"######");
			if (!markFolder.exists()) {
				markFolder.mkdirs();
			}
			try {
				markWriter = new BufferedWriter(new FileWriter(new File(
						logFileInfo.getMarkFullPath())));
				markWriter.write(markInfo.getMarkStr());
				markWriter.flush();
				if (status == ReaderStatus.NORMAL_NOMARK) {
					status = ReaderStatus.NORMAL;
				}
			} catch (IOException e) {
				status = ReaderStatus.NORMAL_NOMARK;
			} finally {
				if (markWriter != null) {
					try {
						markWriter.close();
					} catch (IOException e) {
					}
				}
			}
		}

	}

	/**
	 * 获取markInfo方法 逻辑: 从文件载入读取进度信息，并与对应的日志文件进行比对
	 * 当行号及偏移量与日志文件相符，返回信息，日志文件将接着进度继续读取
	 * 当行号及偏移量与日志文件不符，表示日志文件内容已被更改，读取进度将被清零，日志文件将被重头读取
	 * 
	 * @return
	 */
	public MarkInfo getMarkInfo() {
		BufferedReader markReader = null;
		if (logFileInfo.getMarkPath() == null
				|| logFileInfo.getMarkFullPath() == null) {
			return null;
		}
		File markFolder = new File(logFileInfo.getMarkPath());
		File markFile = new File(logFileInfo.getMarkFullPath());
		MarkInfo fileMarkInfo = null;
		try {
			if (markFile.exists()) {
				markReader = new BufferedReader(new FileReader(markFile));
				fileMarkInfo = new MarkInfo(markReader.readLine());
			} else {
				if (!markFolder.exists()) {
					markFolder.mkdirs();
				}
				markFile.createNewFile();
				fileMarkInfo = new MarkInfo();
			}
		} catch (IOException e) {
			status = ReaderStatus.EXCEPTION;
			return null;
		} finally {
			if (markReader != null) {
				try {
					markReader.close();
				} catch (IOException e) {
				}
			}
		}
		checkMarkInfo(fileMarkInfo);
		return fileMarkInfo;
	}

	/**
	 * 读取信息进度检查 逻辑: 如果读取行数小于等于0，重置markinfo 只有行号与偏移量和日志相符,返回true,并且不重置
	 * 其余都是返回false,并且重置
	 * 
	 * @param fileMarkInfo
	 * @return
	 */
	public boolean checkMarkInfo(MarkInfo fileMarkInfo) {
		LineNumberReader lineNumberReader = null;
		long markLineNo = fileMarkInfo.getLineNo();
		long markOffset = fileMarkInfo.getOffset();
		if (markLineNo <= 0L) {
			fileMarkInfo.reset();
			return false;
		} else {
			try {
				lineNumberReader = new LineNumberReader(new FileReader(
						logFileInfo.getFileFullPath()));
				String message = null;
				long offset = 0L;
				while ((message = lineNumberReader.readLine()) != null) {
					// 需要加上/n的长度
					offset += (message.length() + 1);
					if (markLineNo == lineNumberReader.getLineNumber()) {
						if (markOffset != offset) {
							fileMarkInfo.reset();
							return false;
						}
						break;
					}
				}
				if (lineNumberReader.getLineNumber() < markLineNo) {
					fileMarkInfo.reset();
					return false;
				}
			} catch (IOException e) {
				return false;
			} finally {
				if (lineNumberReader != null) {
					try {
						lineNumberReader.close();
					} catch (IOException e) {
					}
				}
			}
			return true;
		}
	}

	public void releaseIfNeed() {
		status = ReaderStatus.CLOSE;
		if (markInfo != null) {
			mark();
		}
		if (lineNumberReader != null) {
			try {
				lineNumberReader.close();
			} catch (IOException e) {
			}
		}
	}

}
