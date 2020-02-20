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
 * ɾ��������ʱ�ļ���Component
 * 
 */
public class DeleteTempFilesComponent implements Callable {
	private static Log logger = LogFactory
			.getLog(DeleteTempFilesComponent.class);

	public Object onCall(MuleEventContext p_eventContext) throws Exception {
		try {
			logger.debug("ɾ�����Ľ��շ����ļ���ʼ");
			FileUtil fileUtil = FileUtil.getInstance();
			int months = 3;// �����·�Ĭ��Ϊ3����
			if (ITFECommonConstant.MSG_LOG_FILE_RESERVE_MONTHS >= 1) {
				months = ITFECommonConstant.MSG_LOG_FILE_RESERVE_MONTHS;
			}
			Date deleteDate = DateUtils.addMonths(TimeFacade.getCurrentDate(),
					-months);
			String fileName;
			Date fileDate;

			// ɾ�����ձ���������deleteDate���ļ�
			String recvpath = ITFECommonConstant.MSG_FILE_RECV_PATH;
			File recvFile = new File(recvpath);
			if (!recvFile.exists()) {
				recvFile.mkdirs();
			}
			File[] recvFiles = recvFile.listFiles();
			for (File file : recvFiles) {
				fileName = file.getName();
				fileDate = TimeFacade.parseDate(fileName);
				// ����ļ����ڲ�Ϊnull��������ָ��ɾ��������deleteDate,����Ҫɾ���ļ�
				if (fileDate != null && fileDate.before(deleteDate)) {
					fileUtil.deleteFiles(file.getAbsolutePath());
					fileUtil.deleteFile(file.getAbsolutePath());
				}
			}

			// ɾ�����ͱ���������deleteDate���ļ�
			String sendpath = ITFECommonConstant.MSG_FILE_SEND_PATH;
			File sendFile = new File(sendpath);
			if (!sendFile.exists()) {
				sendFile.mkdirs();
			}
			File[] sendFiles = sendFile.listFiles();
			for (File file : sendFiles) {
				fileName = file.getName();
				fileDate = TimeFacade.parseDate(fileName);
				// ����ļ����ڲ�Ϊnull��������ָ��ɾ��������deleteDate,����Ҫɾ���ļ�
				if (fileDate != null && fileDate.before(deleteDate)) {
					fileUtil.deleteFiles(file.getAbsolutePath());
					fileUtil.deleteFile(file.getAbsolutePath());
				}
			}
			logger.debug("ɾ�����Ľ��շ����ļ�����");
		} catch (Throwable e) {
			logger.error("��ʱɾ�����Ľ��շ����ļ���������", e);
		}
		return null;
	}
}
