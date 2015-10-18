package cc.dorado.spence.pipeline;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import cc.dorado.spence.config.Context;
import cc.dorado.spence.entity.SinaWeiboUser;
import cc.dorado.spence.util.SpiderLog;

import com.google.common.collect.Maps;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.jfinal.ext.plugin.monogodb.MongodbPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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
	
	public static final String TABLE = "sw_user";
	
	@Override
	public void process(ResultItems resultItems, Task task) {
		SinaWeiboUser user = resultItems.get("user");
		if(user!=null&&StringUtils.isNotEmpty(user.nick)){
			MongodbPlugin mongodbPlugin = new MongodbPlugin(Context.DATABASE);
			mongodbPlugin.start();
			Record record = new Record();
			for(Field f:SinaWeiboUser.class.getDeclaredFields()){
				try {
					if(f.get(user)!=null){
						record.set(f.getName(), f.get(user));
					}
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			DBObject object = MongoKit.getCollection(TABLE).findOne(new BasicDBObject("id", user.id));
			if(object!=null){
				Map<String, Object> filter = Maps.newHashMap();
				filter.put("id", user.id);
				int n = MongoKit.remove(TABLE, filter);
				SpiderLog.log(getClass()).info("用户id为"+user.id+"的用户数据库中存在"+n+"条旧数据，已经全部删除！");
			}
			MongoKit.save(TABLE, record);
			SpiderLog.log(getClass()).info("用户id为"+user.id+"的用户信息保存成功！");
			mongodbPlugin.stop();
		}
	}
}
