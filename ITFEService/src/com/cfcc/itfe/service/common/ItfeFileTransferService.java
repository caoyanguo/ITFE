package com.cfcc.itfe.service.common;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.AbstractFileTransferService;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.service.ITFELoginInfo;

public class ItfeFileTransferService extends AbstractFileTransferService {
	
	private static Log logger = LogFactory.getLog(ItfeFileTransferService.class);

	FileSystemConfig config;

	/**
	 * ��ȡ�������ϴ��ļ���·��
	 * 
	 * @param �ͻ����ļ���
	 */
	public String genFileName(String clientFileName)
			throws FileTransferException {
		String fileName = null;

		if (clientFileName == null) {
			return null;
		}

		int i = clientFileName.lastIndexOf('\\');
		if (i == -1){
			//���û���ҵ���б�ߣ���ô��Ѱ��б��
			i = clientFileName.lastIndexOf('/');
		}
		if (i != -1) {
			fileName = clientFileName.substring(i + 1);
		}

		logger.debug("���������ص��ļ���:" + fileName);
		String srvFile = config.getRoot() 
				+ ITFECommonConstant.FILE_UPLOAD + ((ITFELoginInfo)this.getLoginInfo()).getSorgcode() + "/" + DateUtil.date2String2(TimeFacade.getCurrentDate()) + "/" + fileName;
		File file = new File(srvFile);
		// ����Ŀ¼
		file.getParentFile().mkdirs();

		return ITFECommonConstant.FILE_UPLOAD + ((ITFELoginInfo)this.getLoginInfo()).getSorgcode() + "/" + DateUtil.date2String2(TimeFacade.getCurrentDate()) + "/" + fileName;

	}

	public String getServiceDescription() {
		return "TAS�ļ��������";
	}

	/**
	 * @return
	 */
	public FileSystemConfig getConfig() {
		return config;
	}

	/**
	 * @param config
	 */
	public void setConfig(FileSystemConfig config) {
		this.config = config;
		File ff = new File(config.getRoot());
		ff.mkdirs();

	}

}
