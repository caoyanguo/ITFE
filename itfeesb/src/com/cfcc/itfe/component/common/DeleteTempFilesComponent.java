package com.cfcc.itfe.component.common;

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.util.DateUtil;

/**
 * 
 * 删除报文临时文件的Component
 * 
 */
public class DeleteTempFilesComponent implements Callable {
	private static Log logger = LogFactory
			.getLog(DeleteTempFilesComponent.class);

	public Object onCall(MuleEventContext p_eventContext) throws Exception {
		try {
			logger.debug("删除报文接收发送文件开始");
			FileUtil fileUtil = FileUtil.getInstance();
			int months = 3;// 保留月份默认为3个月
			if (ITFECommonConstant.MSG_LOG_FILE_RESERVE_MONTHS >= 1) {
				months = ITFECommonConstant.MSG_LOG_FILE_RESERVE_MONTHS;
			}
			Date deleteDate = DateUtils.addMonths(TimeFacade.getCurrentDate(),
					-months);
			String fileName;
			Date fileDate;

			// 删除接收报文中早于deleteDate的文件
			String recvpath = ITFECommonConstant.MSG_FILE_RECV_PATH;
			File recvFile = new File(recvpath);
			if (!recvFile.exists()) {
				recvFile.mkdirs();
			}
			File[] recvFiles = recvFile.listFiles();
			for (File file : recvFiles) {
				fileName = file.getName();
				fileDate = TimeFacade.parseDate(fileName);
				// 如果文件日期不为null并且早于指定删除的日期deleteDate,则需要删除文件
				if (fileDate != null && fileDate.before(deleteDate)) {
					fileUtil.deleteFiles(file.getAbsolutePath());
					fileUtil.deleteFile(file.getAbsolutePath());
				}
			}

			// 删除发送报文中早于deleteDate的文件
			String sendpath = ITFECommonConstant.MSG_FILE_SEND_PATH;
			File sendFile = new File(sendpath);
			if (!sendFile.exists()) {
				sendFile.mkdirs();
			}
			File[] sendFiles = sendFile.listFiles();
			for (File file : sendFiles) {
				fileName = file.getName();
				fileDate = TimeFacade.parseDate(fileName);
				// 如果文件日期不为null并且早于指定删除的日期deleteDate,则需要删除文件
				if (fileDate != null && fileDate.before(deleteDate)) {
					fileUtil.deleteFiles(file.getAbsolutePath());
					fileUtil.deleteFile(file.getAbsolutePath());
				}
			}
			logger.debug("删除报文接收发送文件结束");
		} catch (Throwable e) {
			logger.error("定时删除报文接收发送文件发生错误", e);
		}
		return null;
	}
}
