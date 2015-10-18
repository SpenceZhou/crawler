package cc.dorado.spence.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cc.dorado.spence.util.SpiderLog;

/**
 * Project:crawler
 * FileName:Redis.java
 * PackageName:cc.dorado.spence.config
 * Date:2015年10月18日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:Redis
 * Function:读取redis配置
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class Redis {

	/**
	 * redis，host，port，password配置
	 */
	public static final String REDIS_PATH = "/redis.properties";
	
	public static String host;
	
	public static String port;
	
	public static String password;
	
	static{
		Properties properties = new Properties();
		InputStream in = Object.class.getResourceAsStream(REDIS_PATH);
		if(in==null){
			SpiderLog.log(Redis.class).error("未找到redis配置文件！");
		}else{
			try {
				properties.load(in);
				host = properties.getProperty("host", "127.0.0.1").trim();
				port = properties.getProperty("port", "6379").trim();
				password = properties.getProperty("password").trim();
				in.close();
			} catch (IOException e) {
				SpiderLog.log(Redis.class).debug("读取redis配置文件出现异常！");
				e.printStackTrace();
			}
		}
	}
}
