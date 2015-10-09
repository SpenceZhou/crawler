package cc.dorado.spence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Project:spence
 * FileName:User.java
 * PackageName:cc.dorado.spence.entity
 * Date:2015年7月10日
 * Copyright (C) 2015, zsp@dorado.cc All rights reserved.
 *
 * ClassName:User<br>
 * Function:新浪微博用户信息
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class User{
	public Long id;//用户id
	public String nick;//用户别名
	public int level;//用户等级
	public String gender;//用户性别：M代表男，F代表女，U代表未知
	public Date registration;//注册时间
	public int verify;//是否认证：0：未认证，1：个人认证，2：集团认证
	public int followCount;//关注数量
	public List<Long> follow = new ArrayList<Long>();//关注人id列表
	public int followersCount;//粉丝数量
	public List<Long> followers;//粉丝id列表
	public int weiboCount;//微博数量
	public List<Long> weibo;//微博的messageId列表
}