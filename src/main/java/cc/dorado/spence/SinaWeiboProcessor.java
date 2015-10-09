package cc.dorado.spence;


import java.util.Set;

import org.apache.http.cookie.Cookie;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import cc.dorado.spence.config.PathConfig;
import cc.dorado.spence.config.VerifyCookies;
import cc.dorado.spence.util.String2File;

public class SinaWeiboProcessor implements PageProcessor{

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent("spider");
	public void process(Page page) {
		this.saveHtml(page.getHtml().toString());
		page.putField("content", page.getHtml().xpath("//a[@usercard]/text()").all().toString());
		
	}

	public Site getSite() {
		// 添加登录cookie	
		Set<Cookie> cookies = VerifyCookies.getSinaWeiboCookies();	
		if(cookies!=null){
			for(Cookie cookie:cookies){
				site.addCookie(cookie.getDomain(), cookie.getName(), cookie.getValue());
			}
		}		
		return site;
	}
	
	public static void main(String[] args) {
		Spider.create(new SinaWeiboProcessor()).addUrl("http://weibo.com/5187664653/follow").thread(1).run();
	}
	
	/**
	 * saveHtml:将抓取的数据保存至文件
	 * @param html 抓取数据转换的字符串
	 * @return void
	 */
	private void saveHtml(String html){
		String path = PathConfig.SAVETHMLPATH+"sina.html";
		String2File.StringtoFile(html, path);
	}

}
