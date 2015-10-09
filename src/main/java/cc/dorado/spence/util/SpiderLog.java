
package cc.dorado.spence.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ClassName:SpiderLog<br>
 * Function:系统log日志
 * 
 * @auhter Spence
 * @version 1.0
 * @since JDK 1.6
 */
public class SpiderLog {
	

	@SuppressWarnings("rawtypes")
	public static Log log(Class clz){
		return LogFactory.getLog(clz);
	}
	
	public static Log log(){
		return LogFactory.getLog(SpiderLog.class);
	}
}
