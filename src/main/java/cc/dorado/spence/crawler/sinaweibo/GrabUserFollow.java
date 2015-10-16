package cc.dorado.spence.crawler.sinaweibo;

import java.util.Set;

import org.apache.http.cookie.Cookie;

import cc.dorado.spence.config.VerifyCookies;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Project:crawler
 * FileName:GrabUserFollow.java
 * PackageName:cc.dorado.spence.crawler.sinaweibo
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:GrabUserFollow
 * Function:抓取用户的关注列表和粉丝列表（粉丝和关注由于系统设置只能抓取100条5页*20条）
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class GrabUserFollow implements PageProcessor{

	private Site site = Site.me().setRetryTimes(3).setSleepTime(10000)
			.setUserAgent("spider");
	@Override
	public void process(Page page) {
		
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

}
