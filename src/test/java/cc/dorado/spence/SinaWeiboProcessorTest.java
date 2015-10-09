package cc.dorado.spence;

import org.junit.Test;

import us.codecraft.webmagic.Spider;

public class SinaWeiboProcessorTest {
	@Test
	public void testProcessor(){
		Spider.create(new SinaWeiboProcessor()).addUrl("http://d.weibo.com/").thread(2).run();
		
		System.out.println("dfss");
	}

}
