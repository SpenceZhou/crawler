package cc.dorado.spence.crawler.sinaweibo;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import cc.dorado.spence.config.Context;
import cc.dorado.spence.entity.SinaWeiboUser;
import cc.dorado.spence.pipeline.SinaWeiboUserPipeline;

import com.google.common.collect.Sets;
import com.jfinal.ext.plugin.monogodb.MongoKit;
import com.jfinal.ext.plugin.monogodb.MongodbPlugin;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Project:crawler
 * FileName:GrabUser.java
 * PackageName:cc.dorado.spence.crawler.sinaweibo
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:GrabUser
 * Function:抓取用户信息
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class GrabUser implements PageProcessor{
	
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(10000).setUserAgent("spider");
	
	@Override
	public void process(Page page) {
		SinaWeiboUser user = new SinaWeiboUser();
		String url = page.getRequest().getUrl();
		String id = StringUtils.substring(url, url.lastIndexOf("/")+1);
		user.id = Long.parseLong(id);
		//page.putField("nick", page.getHtml().xpath("//h1[@class='username']/text()"));
		user.nick = page.getHtml().xpath("//h1[@class='username']/text()").get();
		if(StringUtils.isEmpty(user.nick)){
			page.setSkip(true);
		}
		String sexStr = page.getHtml().regex("<i class=\"W_icon icon_pf_f*e*male\">").toString();
		String gender = "U";
		if(sexStr!=null){
			if(sexStr.equals("<i class=\"W_icon icon_pf_male\">")){
				gender = "M";
			}else if(sexStr.equals("<i class=\"W_icon icon_pf_female\">")){
				gender = "F";
			}
		}
		//page.putField("gender", gender);	
		user.gender = gender;
		String levelStr = page.getHtml().regex("<span>Lv\\.\\d\\d*</span>").toString();
			int level = 0;
		if(levelStr!=null){
			level = Integer.parseInt(levelStr.substring(levelStr.indexOf(".")+1, levelStr.lastIndexOf("<")));
		}
		
		//page.putField("level", level);
		user.level = level;
		String verifyStr = page.getHtml().regex("class=\"W_icon icon_verify_c*o*_*v\"").toString();
		int verify = 0;
		if(verifyStr!=null){
			if(verifyStr.equals("class=\"W_icon icon_verify_v\"")){
				verify = 1;
			}else if(verifyStr.equals("class=\"W_icon icon_verify_co_v\"")){
				verify = 2;
			}
			
		}
		//page.putField("verify", verify);
		user.verify = verify;
		//page.putField("followCount", page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[1]//strong/text()"));
		user.followCount = Integer.parseInt(page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[1]//strong/text()").get());
		//page.putField("followersCount", page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[2]//strong/text()"));
		user.followersCount = Integer.parseInt(page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[2]//strong/text()").get());
		//page.putField("weibo", page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[3]//strong/text()"));
		user.weiboCount = Integer.parseInt(page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[3]//strong/text()").get());
		page.putField("user", user);
		
		if(!page.getResultItems().isSkip()){
			Set<String> idSet = Sets.newHashSet();
			String regex = "id=[0-9]{10}";
			String subRegex = "[0-9]{10}";
			idSet.addAll(page.getHtml().regex(regex).regex(subRegex).all());
			for(String s:idSet){
				boolean inSchedure = page.getTargetRequests().contains("http://weibo.com/"+s);
				if(inSchedure){
					continue;
				}
				boolean hasGrab = hasGrab(s);
				if(hasGrab){
					continue;
				}
				page.addTargetRequest("http://weibo.com/"+s);
			}
		}
	}

	
	@Override
	public Site getSite() {
		// 添加登录cookie
		/*Set<Cookie> cookies = VerifyCookies.getSinaWeiboCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				site.addCookie(cookie.getDomain(), cookie.getName(),
						cookie.getValue());
			}
		}*/
		return site;
	}
	
	/**
	 * hasGrab:判断该用户是否已经爬取
	 * @param id
	 * @return
	 */
	private boolean hasGrab(String id){
		MongodbPlugin mongodbPlugin = new MongodbPlugin(Context.DATABASE);
		mongodbPlugin.start();
		DBObject object = MongoKit.getCollection(SinaWeiboUserPipeline.TABLE).findOne(new BasicDBObject("id", id));
		mongodbPlugin.stop();
		if(object!=null){
			return true;
		}
		return false;
	}

	/**
	 * getLastId:返回数据库中最后一个集合
	 * @return
	 */
	public String getLastId(){
		MongodbPlugin mongodbPlugin = new MongodbPlugin(Context.DATABASE);
		mongodbPlugin.start();
		DBCollection dbCollection = MongoKit.getCollection(SinaWeiboUserPipeline.TABLE);	
		DBObject orderBy = new BasicDBObject();
		orderBy.put("_id", -1);
		DBObject lastObject = dbCollection.find().sort(orderBy).one();
		if(lastObject==null){
			return "1191955811";
		}
		return lastObject.get("id").toString();
	}
	
	
	public static void main(String[] args) {	
		GrabUser grabUser = new GrabUser();
		String id = grabUser.getLastId();
		Spider.create(new GrabUser()).addUrl("http://weibo.com/"+id).addPipeline(new SinaWeiboUserPipeline()).run();
	}
}
