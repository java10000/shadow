package com.varicom.shadow.logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.varicom.kafka.client.KafkaProperties;
import com.varicom.kafka.client.Producer;
import com.varicom.shadow.utils.SleepUtil;


@Component
public class LoggerComponent {
	
	private static final int corePoolSize = 2;
	private static final int maxPoolSize = 5;
	private static final int keepAliveTime = 10;
	private static final int workQueue = 2000;
	
	/** 自定义线程池 */
	private static ThreadPoolExecutor loggerThreadPool = new ThreadPoolExecutor(
			corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
			new ArrayBlockingQueue<Runnable>(workQueue),
			new ThreadPoolExecutor.CallerRunsPolicy());

	@PostConstruct
	public void init()
	{
		this.loggerRunnalbe();
	}
	
	/**
	 * 启动异步线程向kafka写入日志数据
	 */
	public void loggerRunnalbe() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Producer producer = new Producer(KafkaProperties.topic);
				while (true) {
					if (!LoggerConstant.LOGGER_QUEUE.isEmpty()) {
						final String logger = LoggerConstant.LOGGER_QUEUE.poll();
						loggerThreadPool.execute(new Runnable() {
							public void run() {
								try {
									System.err.println(logger);
									producer.produce(logger);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									
								}
							}
						});
					}
					else 
					{
						SleepUtil.sleepSeconds(1);
					}
				}
			}
		}).start();
	}
	
	/**
	 * 日志写入缓存队列
	 * @param logger
	 */
	public void write(String logger)
	{
		LoggerConstant.LOGGER_QUEUE.offer(logger);
	}
}
