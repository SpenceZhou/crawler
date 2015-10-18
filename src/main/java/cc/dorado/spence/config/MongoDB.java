package cc.dorado.spence.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cc.dorado.spence.util.SpiderLog;

/**
 * Project:crawler
 * FileName:MongoDB.java
 * PackageName:cc.dorado.spence.config
 * Date:2015年10月18日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:MongoDB
 * Function:读取mongodb配置文件
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class MongoDB {

	/**
	 * MONGODB_PATH，mongodb配置
	 */
	public static final String MONGODB_PATH = "/mongodb.properties";
	
	public static String host;
	
	public static String port;
	
	public static String database;
	
	public static String username;
	
	public static String password;
	
	public static String swUserCollection;
	
	static {
		Properties properties = new Properties();
		InputStream in = Object.class.getResourceAsStream(MONGODB_PATH);
		if(in==null){
			SpiderLog.log(Redis.class).error("未找到mongodb配置文件！");
		}else{
			try {
				properties.load(in);
				host = properties.getProperty("host", "127.0.0.1").trim();
				port = properties.getProperty("port", "27017").trim();
				database = properties.getProperty("database").trim();
				username = properties.getProperty("username").trim();
				password = properties.getProperty("password").trim();
				swUserCollection = properties.getProperty("sw_user_collection").trim();
				in.close();
			} catch (IOException e) {
				SpiderLog.log(Redis.class).debug("读取mongodb配置文件出现异常！");
				e.printStackTrace();
			}
		}
	}
}
