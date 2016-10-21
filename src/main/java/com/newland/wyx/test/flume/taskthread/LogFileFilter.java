package com.newland.wyx.test.flume.taskthread;

import java.io.File;
import java.io.FileFilter;

public class LogFileFilter implements FileFilter{
    
	/**
	 * 过滤文件夹
	 */
	public boolean accept(File file) {
		return file.isFile();
	}

}
