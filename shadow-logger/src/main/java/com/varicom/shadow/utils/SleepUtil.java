package com.varicom.shadow.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author wdw
 *
 */
public class SleepUtil {
	
	  private static Logger logger = LoggerFactory.getLogger(SleepUtil.class);
	
	/**
	 * 线程休眠num秒
	 * @param num
	 */
	public static void sleepSeconds(int num)
	{
		try {
			Thread.sleep(num * 1000);
		} catch (InterruptedException e) {
			logger.error("the thread sleep error ", e);
		}
	}
}
