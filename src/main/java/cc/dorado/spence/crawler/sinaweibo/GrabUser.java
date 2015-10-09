package cc.dorado.spence.crawler.sinaweibo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.processor.PageProcessor;
import cc.dorado.spence.config.PathConfig;
import cc.dorado.spence.entity.User;
import cc.dorado.spence.util.String2File;

/**
 * Project:spence FileName:GrabUser.java
 * PackageName:cc.dorado.spence.crawler.sinaweibo Date:2015年7月8日 Copyright (C)
 * 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:GrabUser<br>
 * Function:抓取用户信息
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
@TargetUrl("http://weibo.com/*")
public class GrabUser implements PageProcessor{
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(10000).setUserAgent("spider");
	
	@Override
	public void process(Page page) {
		page.putField("nick", page.getHtml().xpath("//span[@class='username']/text()"));
		String sexStr = page.getHtml().regex("<i class=\"W_icon icon_pf_f*e*male\">").toString();
		String gender = "U";
		if(sexStr!=null){
			if(sexStr.equals("<i class=\"W_icon icon_pf_male\">")){
				gender = "M";
			}else if(sexStr.equals("<i class=\"W_icon icon_pf_female\">")){
				gender = "F";
			}
		}
		page.putField("gender", gender);		
		String levelStr = page.getHtml().regex("<span>Lv\\.\\d\\d*</span>").toString();
			int level = 0;
		if(levelStr!=null){
			level = Integer.parseInt(levelStr.substring(levelStr.indexOf(".")+1, levelStr.lastIndexOf("<")));
		}
		
		page.putField("level", level);
		String verifyStr = page.getHtml().regex("class=\"W_icon icon_verify_c*o*_*v\"").toString();
		int verify = 0;
		if(verifyStr!=null){
			if(verifyStr.equals("class=\"W_icon icon_verify_v\"")){
				verify = 1;
			}else if(verifyStr.equals("class=\"W_icon icon_verify_co_v\"")){
				verify = 2;
			}
			
		}
		page.putField("verify", verify);
		page.putField("followCount", page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[1]//strong/text()"));
		page.putField("followersCount", page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[2]//strong/text()"));
		page.putField("weibo", page.getHtml().xpath("//div[@class='WB_innerwrap']/table/tbody/tr/td[3]//strong/text()"));
		this.saveHtml(page.getHtml().toString());
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
			System.out.println("cookie");
		}*/
		System.out.println("site");
		return site;
	}
	
	private void saveHtml(String html){
		String path = PathConfig.SAVETHMLPATH+"sina.html";
		String2File.StringtoFile(html, path);
	}
	/**
	 * grab:传入微博用户的id获取微博用户的基本信息
	 * @param id 微博用户的id
	 * @return
	 * @return User 微博用户的基本信息
	 */
	public User grab(Long id){
		Spider.create(new GrabUser()).addUrl("http://weibo.com/"+id).run();
		
		return null;
	}

	public static void main(String[] args) {
		/*Spider.create(new SinaWeiboProcessor()).addPipeline(new FilePipeline());
		MongodbPlugin mongodbPlugin = new MongodbPlugin("127.0.0.1", 27017,
				"crawler");
		mongodbPlugin.start();*/
		// Record record = new Record();
		// record.set("name", "spence");
		// MongoKit.save("sw_user", record);
		// OOSpider.create(Site.me().addStartUrl("http://my.oschina.net/flashsword/blog/145796"),
		// SinaUserinfo.class).run();
		System.out.println("start");
		GrabUser grab = new GrabUser();
		grab.grab(1862172703l);
		System.out.println("end");
	}

	

	

}
