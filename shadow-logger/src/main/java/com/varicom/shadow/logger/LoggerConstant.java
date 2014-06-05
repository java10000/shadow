package com.varicom.shadow.logger;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface LoggerConstant {

	public static final ConcurrentLinkedQueue<String> LOGGER_QUEUE = new ConcurrentLinkedQueue<String>();
}
