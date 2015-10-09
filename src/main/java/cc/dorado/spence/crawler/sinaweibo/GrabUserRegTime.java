package cc.dorado.spence.crawler.sinaweibo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.http.cookie.Cookie;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import cc.dorado.spence.config.VerifyCookies;
import cc.dorado.spence.util.SpiderLog;

/**
 * Project:spence
 * FileName:GrabUserRegTime.java
 * PackageName:cc.dorado.spence.crawler.sinaweibo
 * Date:2015年7月11日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:GrabUserRegTime<br>
 * Function:抓取用户的注册时间
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class GrabUserRegTime implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(10000)
			.setUserAgent("spider");
	private Date date;

	@Override
	public void process(Page page) {
		String dateStr = page.getHtml().xpath("//div[@class='WB_cardwrap S_bg2']/span/span/text()").toString(); 
		this.date = this.formatDate(dateStr);
	}

	@Override
	public Site getSite() {
		// 添加登录cookie
		Set<Cookie> cookies = VerifyCookies.getSinaWeiboCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				site.addCookie(cookie.getDomain(), cookie.getName(),
						cookie.getValue());
			}
		}
		return site;
	}
	/**
	 * grab:获取用户的注册时间
	 * @param id
	 * @return
	 * @return Date
	 */
	public Date grab(Long id){
		Spider.create(new GrabUserRegTime()).addUrl("http://weibo.com/"+id+"?firstfeed=1").run();
		return this.date;
	}
	
	private Date formatDate(String dateStr){
		if(dateStr==null){
			return null;
		}
		String dateString =  dateStr.substring(0, dateStr.indexOf("@")).trim();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		try {
			Date date = sdf.parse(dateString);
			return date;
		} catch (ParseException e) {
			//e.printStackTrace();
			SpiderLog.log(getClass()).info("注册日期格式转换出错！");
		}
		return null;
	}
}
