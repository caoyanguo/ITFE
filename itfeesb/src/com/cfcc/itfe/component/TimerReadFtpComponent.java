package com.cfcc.itfe.component;

//import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.util.FtpUtil;


/**
 * 
 * 山东一本通定时读取Ftp文件任务
 * 
 */
public class TimerReadFtpComponent implements Callable {

	private static Log logger = LogFactory.getLog(TimerReadFtpComponent.class);
	public Object onCall(MuleEventContext eventContext) throws Exception {
		String date = TimeFacade.getCurrentStringTime();
		logger.info("山东一本通定时读取Ftp文件start=================================="+date);
		FtpUtil.downLoadZipFile(date, "");
		logger.info("山东一本通定时读取Ftp文件end=================================="+date);
		return eventContext;
	}
}
