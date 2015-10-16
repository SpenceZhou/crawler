package cc.dorado.spence.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.cookie.Cookie;

import cc.dorado.spence.login.SinaLogin;
import cc.dorado.spence.util.SpiderLog;

import com.google.common.collect.HashMultimap;

/**
 * Project:crawler
 * FileName:VerifyCookies.java
 * PackageName:cc.dorado.spence.config
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:VerifyCookies
 * Function:登录用户验证身份后的cookies
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class VerifyCookies {

	public static HashMultimap<String, Cookie> sinaWeibo = HashMultimap
			.create();

	static {
		// 新浪微博
		setSinaWeiboCookies();
		// 腾讯微博
	}

	/**
	 * getSinaWeiboCookies:获取sina微博的登录cookies
	 * @return
	 * @return Set<Cookie>
	 */
	public static Set<Cookie> getSinaWeiboCookies(){
		Map<String, String> sinaAccount = Account.sinaWeibo;
		if (sinaAccount == null) {
			SpiderLog
					.log(VerifyCookies.class)
					.info("新浪微博用于登录的账号为空，请将账号和密码写于account文件中！");
			return null;
		}
		for(String key : sinaAccount.keySet()){
			if(sinaWeibo.get(key)!=null)
				return sinaWeibo.get(key);
			SpiderLog.log(VerifyCookies.class).info(
					"账号名为" + key + "的sina微博账号模拟登录获取cookie失败！");
		}
		return null;
	}
	
	/**
	 * setSinaWeiboCookies:新浪微博登录Cookies赋值
	 * @return void
	 */
	private static void setSinaWeiboCookies(){
		SinaLogin sinalogin = new SinaLogin();
		Map<String, String> sinaAccount = Account.sinaWeibo;
		if (sinaAccount == null) {
			SpiderLog
					.log(VerifyCookies.class)
					.info("新浪微博用于登录的账号为空，请将账号和密码写于account文件中！");
		} else {
			for (String key : sinaAccount.keySet()) {
				if (!sinaWeibo.containsKey(key)) {
					List<Cookie> sinaCookies = sinalogin.getCookie(key,
							sinaAccount.get(key));
					if (sinaCookies == null) {
						SpiderLog.log(VerifyCookies.class).info(
								"账号名为" + key + "的sina微博账号模拟登录获取cookie失败！");
						break;
					}
					for (Cookie c : sinaCookies) {
						sinaWeibo.put(key, c);
					}

				}
			}
		}
	}
	
}
