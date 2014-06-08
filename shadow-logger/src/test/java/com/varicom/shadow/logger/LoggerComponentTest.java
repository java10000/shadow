package com.varicom.shadow.logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.varicom.shadow.utils.SleepUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:loggerSpringContext.xml")
public class LoggerComponentTest {

	@Autowired
	private LoggerComponent loggerComponent;
	
	@Before
	public void before()
	{
		loggerComponent.loggerRunnalbe();
	}
	
	@Test
	public void TestLogger()
	{
		while (true) {
			String timeString = "time_"+System.currentTimeMillis();
			LoggerConstant.LOGGER_QUEUE.offer(timeString);
			SleepUtil.sleepSeconds(2);
		}
	}
	
	
}
