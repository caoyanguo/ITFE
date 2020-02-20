package com.cfcc.itfe.service.para.tsfinmovepaysub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * @time   12-06-26 14:44:55
 * codecomment: 
 */

public class TsFinmovepaysubService extends AbstractTsFinmovepaysubService {
	private static Log log = LogFactory.getLog(TsFinmovepaysubService.class);	


	/**
	 * ��������֧���б�
	 	 
	 * @generated
	 * @param pageRequest
	 * @param dto
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse finmovepayList(PageRequest pageRequest, IDto dto) throws ITFEBizException {
    	try {
			return CommonFacade.getODB().findRsByDtoPaging(dto, pageRequest);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ҳ��ѯ����",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ҳ��ѯ����",e);
		}
    }

	/**
	 * ��������֧��ɾ��
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
    public void finmovepayDelete(IDto dto) throws ITFEBizException {
    	try {
			DatabaseFacade.getODB().delete(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL,e);
		}
      
    }

	/**
	 * ��������֧��¼��
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
    public void finmovepaySave(IDto dto) throws ITFEBizException {
    	try {
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶκ���������룬��Ŀ�����Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL,e);
		}
    }

	/**
	 * ��������֧���޸�
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
    public void finmovepayModify(IDto dto) throws ITFEBizException {
    	
    	try {
			DatabaseFacade.getODB().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶκ���������룬��Ŀ�����Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL,e);
		}
      
    }

}