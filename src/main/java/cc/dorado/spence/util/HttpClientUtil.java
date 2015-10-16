package cc.dorado.spence.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Project:crawler
 * FileName:HttpClientUtil.java
 * PackageName:cc.dorado.spence.util
 * Date:2015年10月16日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:HttpClientUtil
 * Function:
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class HttpClientUtil {
	public static final String PARAMETERERROR = "ParameterError";
	public static final String CLIENTPROTOCOLEXCEPTION = "ClientProtocolExceptions";
	public static final String IOEXCEPTION = "IOExceptions";
	
	public static CloseableHttpClient httpclient = HttpClients.createDefault();
	
	public static String getPostResponseJson(String url,List <NameValuePair> params,String chartset){		
		HttpPost post = new HttpPost(url);
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			post.setEntity(new UrlEncodedFormEntity(params,chartset));
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
			return HttpClientUtil.PARAMETERERROR;
		}
		try {
			CloseableHttpResponse response = httpclient.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				return EntityUtils.toString(response.getEntity());
			}
			return String.valueOf(response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			//e.printStackTrace();
			return HttpClientUtil.CLIENTPROTOCOLEXCEPTION;
		} catch (IOException e) {
			//e.printStackTrace();
			return HttpClientUtil.IOEXCEPTION;
		}	
	}
	
	public static String getResponseJson(String url){
		HttpGet get = new HttpGet(url);
		get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
		CloseableHttpResponse response;
		try {
			response = httpclient.execute(get);
			if(response.getStatusLine().getStatusCode() == 200){
				return EntityUtils.toString(response.getEntity());
			}
			return String.valueOf(response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			//e.printStackTrace();
			return HttpClientUtil.CLIENTPROTOCOLEXCEPTION;
		} catch (IOException e) {
			//e.printStackTrace();
			return HttpClientUtil.IOEXCEPTION;
		}
		
		
	}
}
