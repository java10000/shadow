package com.varicom.shadow.core.util;

import java.io.IOException;
import java.util.Properties;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件工具类
 * @author wdw
 *
 */
public class FileUtil {
	
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 读取properties文件
	 * @param fileName
	 * @return
	 */
	public static Properties readProp(String fileName) {
		Properties properties = new Properties();
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			logger.error("properties file is not found", e);
		}
		
		return properties;
	}
	
}
