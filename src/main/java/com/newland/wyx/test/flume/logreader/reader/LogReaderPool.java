package com.newland.wyx.test.flume.logreader.reader;

import java.util.ArrayList;  
import java.util.HashMap;
import java.util.List;
import java.util.Map;





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.logreader.bean.LogFileInfo;

/**
 * 文件读取管理器
 * @author 吴越骁
 *
 */
public class LogReaderPool {
	public Logger logger = LoggerFactory.getLogger(LogReaderPool.class);
	
	/**
	 * 读取器集合
	 */
	private List<ILogReader> readerList;
    
	/**
	 * 读取下标
	 */
	private int readerIndex;
    
	/**
	 * 文件映射表
	 */
	private Map<String, ILogReader> readerMap;
	
	public LogReaderPool() {
		readerList = new ArrayList<ILogReader>();
		readerMap = new HashMap<String, ILogReader>();
	}
    
	/**
	 * 根据下标寻找相应的文件读取器(未实现配置项 readcountbyonce)
	 * @return 当前读取器
	 */
	public synchronized ILogReader nextRead() {
		int readsize = readerList.size() ;
		if (readsize == 0)
			return null;
		if (readerIndex >= readsize)
			readerIndex = 0;
		ILogReader currentReader = readerList.get(readerIndex);
		readerIndex++;
		ReaderStatus status = currentReader.status();
		if (status == ReaderStatus.NORMAL
				|| status == ReaderStatus.NORMAL_NODATA
				|| status == ReaderStatus.NORMAL_NOMARK) {
			return currentReader;
		}
		return null;
	}
    
	/**
	 * 容器新增读取器
	 * 根据filename+path作为key值
	 * @param reader
	 */
	public synchronized void addReader(ILogReader reader) {
		 String filePath = parseFilePath(reader);
		 if(readerMap.get(filePath) == null){
			readerList.add(reader);
			readerMap.put(filePath, reader);
		 }
	}
	
	/**
	 * 每个reader执行mark方法
	 */
	public synchronized void markAll(){
		for (ILogReader reader : readerList) {
			reader.mark();
		}
	}

	
	public synchronized int currentReaderSize() {
		return readerList.size();
	}
	
	public synchronized boolean hasThisReader(LogFileInfo fileInfo) {
		return readerMap.get(fileInfo.getFilePath() + fileInfo.getFileName()) != null;
	}

	// 匹配readMap的key（key为日志文件全路径）
	private String parseFilePath(ILogReader logReader) {
		LogFileInfo fileInfo = logReader.getLogFileInfo();
		return fileInfo.getFilePath() + fileInfo.getFileName();
	}
	
	/**
	 * 获取reader快照
	 * @return
	 */
	public synchronized List<ILogReader> getReaderSnapshot() {
		return new ArrayList<ILogReader>(readerList);
	}
	
	/**
	 * 删除一些reader方法
	 * @param removeReaderList
	 */
	public synchronized void removeReader(List<ILogReader> removeReaderList) {
		for (ILogReader logReader : removeReaderList) {
			readerList.remove(logReader);
			readerMap.remove(parseFilePath(logReader));
		}
	}
}
