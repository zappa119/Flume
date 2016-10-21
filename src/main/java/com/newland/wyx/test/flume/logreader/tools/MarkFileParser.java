package com.newland.wyx.test.flume.logreader.tools;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 工具类，获取mark文件的中的行数，也就是已经读取的行数  mark文件的格式为  lineNO : offset
 * @author 吴越骁
 *
 */
public class MarkFileParser {
	private MarkFileParser() {

	}

	public static long parseLine(File markFile) throws IOException {
		if (markFile.exists()) {
			BufferedReader markReader = null;
			try {
				markReader = new BufferedReader(new FileReader(markFile));
				String line = markReader.readLine();
				if (line == null || line.indexOf(":") < 0) {
					return 0;
				}
				return Long.parseLong(line.split(":")[0]);
			} catch (IOException e) {
				throw e;
			} finally {
				if (markReader != null) {
					markReader.close();
				}
			}

		}

		return -1;
	}
}
