package cc.dorado.spence.pipeline;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import cc.dorado.spence.config.Context;
import cc.dorado.spence.config.MongoDB;
import cc.dorado.spence.entity.SinaWeiboUser;
import cc.dorado.spence.util.SpiderLog;

import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * Project:crawler
 * FileName:SinaWeiboUserPipeline.java
 * PackageName:cc.dorado.spence.pipeline
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:SinaWeiboUserPipeline
 * Function:持久化user信息
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class SinaWeiboUserPipeline implements Pipeline{
	
	@Override
	public void process(ResultItems resultItems, Task task) {
		SinaWeiboUser user = resultItems.get("user");
		if(user!=null&&StringUtils.isNotEmpty(user.nick)){
			Document document = new Document();
			for(Field f:SinaWeiboUser.class.getDeclaredFields()){
				try {
					if(f.get(user)!=null){
						document.append(f.getName(), f.get(user));
					}
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			
			MongoClient client = new MongoClient(Context.DATABASE_ADDRESS, Arrays.asList(Context.MONGODB_CREDENTIAL));
			MongoDatabase database = client.getDatabase(MongoDB.database);
			MongoCollection<Document> collection = database.getCollection(MongoDB.swUserCollection);
			Document object = collection.find(Filters.eq("id", user.id)).first();
	
			if(object!=null){
				Map<String, Object> filter = Maps.newHashMap();
				filter.put("id", user.id);
				long n = collection.deleteMany(Filters.eq("id", user.id)).getDeletedCount();
				SpiderLog.log(getClass()).info("用户id为"+user.id+"的用户数据库中存在"+n+"条旧数据，已经全部删除！");
			}
			collection.insertOne(document);
			SpiderLog.log(getClass()).info("用户id为"+user.id+"的用户信息保存成功！");
			client.close();
		}
	}
}
