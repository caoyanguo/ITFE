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
	      logger.error("文件名为空");
	      throw new FileTransferException("文件名为空!");
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
	 * 获取服务器上传文件的路径
	 * 
	 * @param 客户端文件名
	 */
	public String genFileName(String clientFileName)
			throws FileTransferException {
		String fileName = null;

		if (clientFileName == null) {
			return null;
		}

		int i = clientFileName.lastIndexOf('\\');
		if (i == -1){
			//如果没有找到反斜线，那么就寻找斜线
			i = clientFileName.lastIndexOf('/');
		}
		if (i != -1) {
			fileName = clientFileName.substring(i + 1);
		}

		logger.debug("服务器返回的文件名:" + fileName);
		String srvFile = config.getRoot() 
				+ ITFECommonConstant.FILE_UPLOAD + ((ITFELoginInfo)this.getLoginInfo()).getSorgcode() + "/" + DateUtil.date2String2(TimeFacade.getCurrentDate()) + "/" + fileName;
		File file = new File(srvFile);
		// 创建目录
		file.getParentFile().mkdirs();

		return ITFECommonConstant.FILE_UPLOAD + ((ITFELoginInfo)this.getLoginInfo()).getSorgcode() + "/" + DateUtil.date2String2(TimeFacade.getCurrentDate()) + "/" + fileName;

	}

	public String getServiceDescription() {
		return "TAS文件传输服务。";
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
