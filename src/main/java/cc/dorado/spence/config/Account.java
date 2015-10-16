package cc.dorado.spence.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cc.dorado.spence.util.SpiderLog;

import com.google.common.collect.Maps;

/**
 * Project:crawler
 * FileName:Account.java
 * PackageName:cc.dorado.spence.config
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:Account
 * Function:模拟登录用户
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class Account {
	/**
	 * accountPath:account路径
	 */
	public static final String SINAWEIBO_PATH = "src/main/java/sinaweibo";
	/**
	 * sinaWeibo:新浪微博用户
	 */
	public static Map<String, String> sinaWeibo = new HashMap<String, String>();
	
	static{
		sinaWeibo = readAccount(SINAWEIBO_PATH);
	}

	/**
	 * readAccount:读取账户文件
	 * @param accountPath
	 * @return
	 * @return Map<String,String>
	 */
	@SuppressWarnings("resource")
	public static Map<String, String> readAccount(String accountPath){
		Map<String, String> map = Maps.newHashMap();
		File account = new File(accountPath);
		if(!account.exists()){
			SpiderLog.log(Account.class).warn("账户文件不存在");
			return map;
		}
		String user = "";//用户
		try {
			FileReader fileReader = new FileReader(account);
			BufferedReader reader = new BufferedReader(fileReader);
			while((user=reader.readLine())!=null){
				String username = StringUtils.substring(user, 0, user.indexOf(","));
				String password = StringUtils.substring(user, user.indexOf(",")+1);
				map.put(username, password);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
}
