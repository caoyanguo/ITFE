package com.cfcc.itfe.service.para.tsoperationmodel;

import java.io.File;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time   09-10-19 21:30:50
 * codecomment: 
 */

public class TsOperationmodelService extends AbstractTsOperationmodelService {
	private static Log log = LogFactory.getLog(TsOperationmodelService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段模版ID，顺序号已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段模版ID，顺序号已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

	/**
	 * 模版上载
	 */
    public String uploadModel(String filePath) throws ITFEBizException {
    	try{
    		//得到上传文件的完整路径
    		String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			//读取文件的全部内容，这个变量的内容以后就不要用修改了，可以直接把这些内容写入到接收目录中
			File file = new File(fileFullName);
			String fileName = file.getName();//文件名
    		//获取当前时间
    		Timestamp now = TSystemFacade.getDBSystemTime();
    		//模版保存路径
    		String fileSavePath = "/model/" + DateUtil.date2String3(now) + "/";
			//创建保存目录
			FileUtil.getInstance().createDir(ITFECommonConstant.FILE_ROOT_PATH + fileSavePath);
			//将接收到的文件转移到接收目录中
			String fileSaveName = ITFECommonConstant.FILE_ROOT_PATH + fileSavePath + fileName;
			FileCopyUtils.copy(file, new File(fileSaveName));
			//删除已经上传的文件
			file.delete();
			return (fileSavePath + fileName);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("模版上载时发生错误", e);
		} 
	}

}