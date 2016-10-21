package com.newland.wyx.test.flume.source;

import java.io.UnsupportedEncodingException; 
import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.wyx.test.flume.logreader.bean.LogInfo;
import com.newland.wyx.test.flume.logreader.clientadpter.ReadPlatformToolsForFlume;
import com.newland.wyx.test.flume.logreader.conf.AbstractLogReadPlatform;
import com.newland.wyx.test.flume.logreader.conf.LogReadPlatform;
import com.newland.wyx.test.flume.logreader.conf.LogReadPlatformConf;

/**
 * 入口类
 * @author 吴越骁
 *
 */
public class TestSource extends AbstractSource implements Configurable,
		PollableSource {

	private Logger logger = LoggerFactory.getLogger(TestSource.class);

	private String sourceName;

	private LogReadPlatformConf conf;

	private LogReadPlatform logReadPlatform;

	private List<Event> error;
    
	/**
	 * flume框架先执行configure方法
	 * 自己配置的source已有flume框架实现传入
	 */
	public void configure(Context context) {
		sourceName = context.getString("sourceName", "OpenlatLogSource");
		conf = ReadPlatformToolsForFlume.parse(context);
		logReadPlatform = new AbstractLogReadPlatform();
		logReadPlatform.init(conf, sourceName);
	}
    
	/**
	 * flume框架实现
	 * configure方法执行初始化完成后执行start方法
	 * 各线程及监控启动(目前只实现newFile扫描线程)
	 */
	@Override
	public synchronized void start() {
		super.start();
		logReadPlatform.start();
	}
    
	/**
	 * 各线程启动后
	 * flume框架间隔执行process方法
	 * 读取扫描到的文件、将日志信息转换成event
	 * 调用flume框架的getChannelProcessor().processEventBatch(events)方法传入channel
	 */
	public Status process() throws EventDeliveryException {
		Status status = null;
		List<Event> events = null;
		try {
			if (error == null || error.isEmpty()) {
				List<LogInfo> nginxLogList = logReadPlatform.read();
				events = createEvents(nginxLogList);
			} else {
				events = error;
			}
			if (events.isEmpty()) {
				Thread.sleep(500);
				return Status.READY;
			}
			getChannelProcessor().processEventBatch(events);
			if (error != null) {
				error = null;
			}
			status = Status.READY;
		} catch (InterruptedException e) {
			status = Status.READY;
		} catch (Throwable t) {
			error = events;
			status = Status.BACKOFF;
			logger.error(sourceName + " source exception.", t);
		}
		return status;
	}
    
	/**
	 * 创建Event,按照需求自己实现逻辑
	 * @param logInfoArray
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private List<Event> createEvents(List<LogInfo> logInfoArray)
			throws UnsupportedEncodingException {
		List<Event> events = new ArrayList<Event>(logInfoArray.size());
		for (LogInfo logInfo : logInfoArray) {
			Event event = EventBuilder.withBody(logInfo.getLog().getBytes());
			event.getHeaders().put("logFileName", logInfo.getFileFullPath());
			events.add(event);
		}
		return events;
	}

}
