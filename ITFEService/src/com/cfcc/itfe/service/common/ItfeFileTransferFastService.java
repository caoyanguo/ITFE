package com.cfcc.itfe.service.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
import com.cfcc.jaf.core.service.filetransfer.support.AbstractFileTransferService;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;

public class ItfeFileTransferFastService extends AbstractFileTransferService {
	
	private static Log logger = LogFactory.getLog(ItfeFileTransferFastService.class);

	FileSystemConfig config;

	@Override
	public Long writeFile(String fileName, Long lStart, Long lLength, byte[] b)
			throws FileTransferException {
		
		/*ExecutorService es = new 
		if (fileName == null)
	    {
	      logger.error("�ļ���Ϊ��");
	      throw new FileTransferException("�ļ���Ϊ��!");
	    }

	    fileName = getConfig().getRoot() + fileName;

	    RandomAccessFile outFile = null;
	    try
	    {
	      outFile = new RandomAccessFile(fileName, "rws");

	      outFile.seek(lStart.longValue());
	      outFile.write(b, 0, lLength.intValue());
	      outFile.close();
	      localLong = lLength;
	    }
	    catch (FileNotFoundException e)
	    {
	      Long localLong;
	      logger.error(e);

	      throw FileTransferException.valueOf(e, fileName);
	    }
	    catch (IOException e)
	    {
	      logger.error(e);

	      throw FileTransferException.valueOf(e, fileName);
	    }
	    finally
	    {
	      try
	      {
	        if (outFile != null)
	          outFile.close();
	      }
	      catch (Exception e1)
	      {
	      }
	    }*/
		return null;
	}
	
	public static class UploadFileHandler{
		
	}
	
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
