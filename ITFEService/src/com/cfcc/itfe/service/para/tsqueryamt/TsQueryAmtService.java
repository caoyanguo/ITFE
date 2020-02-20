package com.cfcc.itfe.service.para.tsqueryamt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author caoyg
 * @time   15-03-26 16:46:52
 * codecomment: 
 */

public class TsQueryAmtService extends AbstractTsQueryAmtService {
	private static Log log = LogFactory.getLog(TsQueryAmtService.class);	


	/**
	 * ����	 
	 * @generated
	 * @param addinfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto addinfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().create(addinfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("�������������+ҵ������+��������ʶ����Ӧ��¼ �Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
    }

	/**
	 * ɾ��	 
	 * @generated
	 * @param delInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto delInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().delete(delInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}  
    }

	/**
	 * �޸�	 
	 * @generated
	 * @param modifyInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto modifyInfo(IDto modifyInfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().update(modifyInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�������������+ҵ������+��������ʶ����Ӧ��¼ �Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
      return null;
    }

}