package com.cfcc.itfe.service.recbiz.voucherattachservice;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
/**
 * @author Administrator
 * @time   14-12-22 17:45:47
 * codecomment: 
 */
@SuppressWarnings("unchecked")
public class VoucherAttachServiceService extends AbstractVoucherAttachServiceService {
	private static Log log = LogFactory.getLog(VoucherAttachServiceService.class);	


	/**
	 * 查找文件名称(带路径)
	 	 
	 * @generated
	 * @param filePath
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List getFileList(String filePath) throws ITFEBizException {
    	filePath = filePath.replace("/", File.separator).replace("\\",File.separator);
    	// 得到文件上传配置
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		// 文件上传根路径
		String root = sysconfig.getRoot();
		root = root.replaceAll(root, filePath);
    	return FileUtil.getInstance().listFileAbspath(filePath);
    }

}