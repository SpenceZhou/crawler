package cc.dorado.spence.login;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import cc.dorado.spence.util.HttpClientUtil;
import cc.dorado.spence.util.SpiderLog;

import com.jayway.jsonpath.JsonPath;

/**
 * Project:crawler
 * FileName:SinaLogin.java
 * PackageName:cc.dorado.spence.login
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:SinaLogin
 * Function:模拟新浪微博登陆，暂未加验证码部分
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class SinaLogin {	
	/**
	 * getPerLogin:预登陆获取rsakey
	 * @param username
	 * @return
	 * @return LoginParameter
	 */
	private LoginParameter getPerLogin(String username){
		LoginParameter parameter = new LoginParameter();
		String usernameBase64 = Base64.encodeBase64String(username.getBytes());
		String usernameUrl = "";
		try {
			usernameUrl = URLEncoder.encode(usernameBase64, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			SpiderLog.log().info("新浪微博模拟登录，URL编码失败！");
		}
		/**
		 * 详情跟踪：http://login.sina.com.cn/  2015-7-7
		 */
		String url = "http://login.sina.com.cn/sso/prelogin.php?"
				+ "entry=account&callback=sinaSSOController.preloginCallBack&"
				+ "su="+usernameUrl+"&rsakt=mod&"
				+ "client=ssologin.js(v1.4.15)&_="+getTimestamp();
		
		String response = HttpClientUtil.getResponseJson(url);
		String responseJson = response.substring(response.indexOf("(")+1, response.lastIndexOf(")"));
		parameter.servertime = Long.parseLong(JsonPath.read(responseJson, "$.servertime").toString());
		parameter.nonce = JsonPath.read(responseJson, "$.nonce");
		parameter.publicKey = JsonPath.read(responseJson, "$.pubkey");
		parameter.rsakv = JsonPath.read(responseJson, "$.rsakv");
		return parameter;
	}
	

	/**
	 * encrypt:新浪微博加密算法
	 * @return
	 * @return String
	 */
	private String encrypt(String publickey, Long servertime, String nonce, String pw){
		String jsFileName = "src/main/java/encrypt.js";
		
		ScriptEngineManager engineManager = new ScriptEngineManager();
		ScriptEngine engine = engineManager.getEngineByName("JavaScript");
		try {
			engine.eval(new java.io.FileReader(jsFileName));
			Invocable inv = (Invocable) engine;
			Object obj = inv.invokeFunction("getpwd", pw, servertime, nonce, publickey);
			
			return obj.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			SpiderLog.log(getClass()).info("未找到新浪微博登录加密js文件！");
		} catch (ScriptException e) {
			e.printStackTrace();
			SpiderLog.log(getClass()).info("新浪微博登录加密js文件，解析出错！");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			SpiderLog.log(getClass()).info("新浪微博登录加密js文件无调用！");
		}
		
		
		return null;
	}
	/**
	 * getTimestamp:获取当前时间戳
	 * @return
	 * @return long
	 */
	private Long getTimestamp() {
        Date now = new Date();
        return now.getTime();
    }

	/**
	 * getLogin:模拟新浪微博登陆，获取登录后的json串
	 * @param username
	 * @param password
	 * @return
	 * @return String
	 */
	private String getLogin(String username, String password){		
		LoginParameter paramter = this.getPerLogin(username);		
		String sp = this.encrypt(paramter.publicKey, paramter.servertime, paramter.nonce, password);				
		String url = "https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.15)&_="+getTimestamp();		        
        List <NameValuePair> params = new ArrayList<NameValuePair>();       
        params.add(new BasicNameValuePair("entry", "account"));
        params.add(new BasicNameValuePair("gateway", "1"));
        params.add(new BasicNameValuePair("savestate", "30"));
        params.add(new BasicNameValuePair("from", ""));
        params.add(new BasicNameValuePair("useticket", "0"));
        params.add(new BasicNameValuePair("pagerefer", ""));
        params.add(new BasicNameValuePair("vsnf", "1"));
        params.add(new BasicNameValuePair("su", Base64.encodeBase64String(username.getBytes())));
        params.add(new BasicNameValuePair("service", "sso"));
        params.add(new BasicNameValuePair("servertime", this.getTimestamp().toString()));
        params.add(new BasicNameValuePair("nonce", paramter.nonce));
        params.add(new BasicNameValuePair("pwencode", "rsa2"));
        params.add(new BasicNameValuePair("rsakv", paramter.rsakv));
        params.add(new BasicNameValuePair("sp", sp));
        params.add(new BasicNameValuePair("encoding", "UTF-8"));
        params.add(new BasicNameValuePair("prelt", "83"));
        params.add(new BasicNameValuePair("returntype", "TEXT"));
        params.add(new BasicNameValuePair("domain", "sina.com.cn"));
        params.add(new BasicNameValuePair("cdult", "3"));
        params.add(new BasicNameValuePair("sr", "1920*1080"));     
        String response = HttpClientUtil.getPostResponseJson(url, params, "UTF-8");
		return response;
	}
	
	/**
	 * getCookieUrl:获取登录成功后的包含cookie的url
	 * @param username
	 * @param password
	 * @return
	 * @return String 如果登录成功返回保存有登录成功cookie的url，登录失败则返回空。
	 */
	
	/***********************************************
	 * 失败时返回json格式
	 * {
  	 *		"retcode": "101",
  	 *		"reason": "登录名或密码错误"
	 * }
	 * 
	 *********************************************/
	
	/***********************************************
	 * 成功时返回json格式
	 * {
  	 *		"retcode": "0",
  	 *		"uid": "5285128267",
  	 *		"nick": "SpenceZhou",
  	 *		"crossDomainUrlList": [
     *			"https://passport.weibo.com/wbsso/login?ticket=ST-NTI4NTEyODI2Nw%3D%3D-1436249325-hk-D3E3813C75FFD92E4C5278468BE27748&ssosavestate=1467785325",
     *			"https://crosdom.weicaifu.com/sso/crosdom?action=login&savestate=1467785325",
     *			"http://passport.97973.com/sso/crossdomain?action=login&savestate=1467785325",
     *			"http://passport.weibo.cn/sso/crossdomain?action=login&savestate=1"
  	 *		]
	 *	}
	 * 返回的第一个url可以获取保存登录信息的cookie
	 *********************************************/
	private String getCookieUrl(String username, String password){
		String response = this.getLogin(username, password);	
		String retcode = JsonPath.read(response, "$.retcode");
		
		if(retcode!=null&&retcode.equals("0")){//登录成功
			return JsonPath.read(response, "$.crossDomainUrlList.[0]");
		}
		String reason = JsonPath.read(response, "$.reason");
		SpiderLog.log(getClass()).info("用户名为："+username+"的用户登录出现异常，返回代码为"+retcode+"异常信息为："+reason);
		return null;
	}
	
	
	/**
	 * getCookie:将cookie信息返回
	 * @param username
	 * @param password
	 * @return List<Cookie>
	 */
	public List<Cookie> getCookie(String username, String password){
		String url = this.getCookieUrl(username, password);
		
		if(url==null){
			SpiderLog.log(getClass()).info("新浪微博模拟登录获取包含cookie的url失败！");
			return null;
		}
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpClientContext context = new HttpClientContext();
		HttpGet get = new HttpGet(url);
		get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(get,context);
			if(response.getStatusLine().getStatusCode() == 200){
				return context.getCookieStore().getCookies();				
			}
		} catch (ClientProtocolException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return null;
	}
}

class LoginParameter{
	public Long servertime;
	public String nonce;
	public String publicKey;
	public String rsakv;
}