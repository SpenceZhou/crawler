package cc.dorado.spence.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


/**
 * ClassName:String2File<br>
 * Function:将字符串写入文件
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.7
 */
public class String2File {
	
	/**
	 * JsontoFile:将字符串写入到指定路径的文件中
	 * @param data 字符串数据
	 * @param path 文件地址
	 * @return void
	 */
	public static void StringtoFile(String data,String path){		
		
		try {
			File file = new File(path);
			FileUtils.writeStringToFile(file, data);
			SpiderLog.log(String2File.class).info("保存文件至"+path+"成功!");
		} catch (IOException e) {
			SpiderLog.log(String2File.class).error("IO异常，保存文件失败！");
			e.printStackTrace();
		}
	}

	/**
	 * JsontoFile:将字符串以指定的编码集写入到指定路径的文件中
	 * @param data 字符串数据
	 * @param path 文件地址
	 * @param encoding 编码集（UTF-8、GBK等）
	 * @return void
	 */
	public static void StringtoFile(String data, String path,  String encoding){
		
		try {
			File file = new File(path);
			FileUtils.writeStringToFile(file, data, encoding);
			SpiderLog.log(String2File.class).info("保存文件至"+path+"成功!");
		} catch (IOException e) {
			SpiderLog.log(String2File.class).error("IO异常，保存文件失败！");
			e.printStackTrace();
		}
	}
	

}
