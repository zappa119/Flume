package com.newland.wyx.test.flume.logreader.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * 工具类，获取文件的总行数
 * @author 吴越骁
 *
 */
public class LogFileLineCountParser {
	private LogFileLineCountParser() {

	}

	public static long getLogFileLineCount(File logFile) throws IOException {
		FileReader fr = null;
		LineNumberReader lnr = null;
		try {
			fr = new FileReader(logFile);
			lnr = new LineNumberReader(fr);
			lnr.skip(logFile.length());
			return lnr.getLineNumber();
		} catch (IOException e) {
			throw e;
		} finally {
			if (lnr != null) {
				try {
					lnr.close();
				} catch (IOException e) {
				}
			}
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
