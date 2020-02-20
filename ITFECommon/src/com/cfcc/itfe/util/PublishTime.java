package com.cfcc.itfe.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 获取打包时间
 * @author t60
 *
 */
public class PublishTime {
	private static Log log = LogFactory.getLog(PublishTime.class);
	public String getTime(){
		log.info("用户工作目录："+new File(System.getProperty("user.dir")).getAbsoluteFile());
		long lt=new File(System.getProperty("user.dir")+"/plugins/itfe_1.0.0.jar").lastModified();
		return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(lt));
		
	}
}
