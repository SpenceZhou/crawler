package cc.dorado.spence.config;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * Project:crawler
 * FileName:Context.java
 * PackageName:cc.dorado.spence.config
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:Context
 * Function:配置文件
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class Context {

	/**
	 * SAVETHMLPATH:保存抓取html文件的地址
	 */
	public static final String SAVETHMLPATH = "../html/";

	/**
	 * JEDIS_POOL:redis连接池
	 */
	public static final JedisPool JEDIS_POOL = new JedisPool(new JedisPoolConfig(), Redis.host, Integer.parseInt(Redis.port), 1000, Redis.password);

	/**
	 * DATABASE_ADDRESS:mongodb 数据库地址
	 */
	public static final ServerAddress DATABASE_ADDRESS = new ServerAddress(MongoDB.host, Integer.parseInt(MongoDB.port));
	
	/**
	 * MONGODB_CREDENTIAL:mongodb数据库证书，用于权限认证
	 */
	public static final MongoCredential MONGODB_CREDENTIAL = MongoCredential.createScramSha1Credential(MongoDB.username, MongoDB.database, MongoDB.username.toCharArray());
}
