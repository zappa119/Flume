package com.newland.wyx.test.flume.logreader.clientadpter;

import java.util.List; 

import org.apache.flume.Context;

import com.google.common.collect.Lists;
import com.newland.wyx.test.flume.logreader.bean.FolderScanInfo;
import com.newland.wyx.test.flume.logreader.conf.LogReadPlatformConf;

/**
 * readplatform工具类
 * 
 * @author 吴越骁
 *
 */
public class ReadPlatformToolsForFlume {

	/**
	 * 传入flume的context配置,转换成对应的readerplatform类
	 * 
	 * @param context
	 * @return
	 */
	public static LogReadPlatformConf parse(Context context) {
		LogReadPlatformConf conf = null;
		String readPlatformType = context.getString("readPlatformType");
		int scanNewFileTaskIntervalMinute = Integer.parseInt(context
				.getString("scanNewFileTaskIntervalMinute"));
		if ("abstract".equalsIgnoreCase(readPlatformType)) {
			int i = 1;
			List<FolderScanInfo> list = Lists.newArrayList();
			while (true) {
				String logFolder = context.getString("f" + i + ".logFolder");
				if (logFolder == null) {
					break;
				}
				FolderScanInfo folderScanInfo = new FolderScanInfo(logFolder,
						context.getString("f" + i + ".markFolder"),
						context.getString("f" + i + ".bakFolder"));
				list.add(folderScanInfo);
				i++;
			}
			conf = new LogReadPlatformConf(list, scanNewFileTaskIntervalMinute);
			conf.setMarkTaskIntervalSecond(context.getInteger("markTaskIntervalSecond",0));
			conf.setDelTaskIntervalSecond(context.getInteger("delTaskIntervalSecond",0));
			conf.setMaxOpenTimeMinute(context.getInteger("maxOpenTimeMinute",0));
			conf.setMaxIdleTimeMinute(context.getInteger("maxIdleTimeMinute",0));
			conf.setNewDelTaskIntervalMinute(context.getInteger("newDelTaskIntervalSecond",0));

		}
		return conf;
	}
}
