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
	 * ����	 
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
				throw new ITFEBizException("�ֶ�ģ��ID��˳����Ѵ��ڣ������ظ�¼�룡", e);
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
				throw new ITFEBizException("�ֶ�ģ��ID��˳����Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

	/**
	 * ģ������
	 */
    public String uploadModel(String filePath) throws ITFEBizException {
    	try{
    		//�õ��ϴ��ļ�������·��
    		String fileFullName = ITFECommonConstant.FILE_ROOT_PATH + filePath;
			//��ȡ�ļ���ȫ�����ݣ���������������Ժ�Ͳ�Ҫ���޸��ˣ�����ֱ�Ӱ���Щ����д�뵽����Ŀ¼��
			File file = new File(fileFullName);
			String fileName = file.getName();//�ļ���
    		//��ȡ��ǰʱ��
    		Timestamp now = TSystemFacade.getDBSystemTime();
    		//ģ�汣��·��
    		String fileSavePath = "/model/" + DateUtil.date2String3(now) + "/";
			//��������Ŀ¼
			FileUtil.getInstance().createDir(ITFECommonConstant.FILE_ROOT_PATH + fileSavePath);
			//�����յ����ļ�ת�Ƶ�����Ŀ¼��
			String fileSaveName = ITFECommonConstant.FILE_ROOT_PATH + fileSavePath + fileName;
			FileCopyUtils.copy(file, new File(fileSaveName));
			//ɾ���Ѿ��ϴ����ļ�
			file.delete();
			return (fileSavePath + fileName);
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("ģ������ʱ��������", e);
		} 
	}

}