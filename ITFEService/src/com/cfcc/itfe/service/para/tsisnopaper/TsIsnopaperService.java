package com.cfcc.itfe.service.para.tsisnopaper;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsIsnopaperDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.service.para.tsisnopaper.AbstractTsIsnopaperService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2admin
 * @time   14-05-14 16:23:02
 * codecomment: 
 */

public class TsIsnopaperService extends AbstractTsIsnopaperService {
	private static Log log = LogFactory.getLog(TsIsnopaperService.class);	


	/**
	 * ����
	 	 
	 * @generated
	 * @param IDto
	 * @throws ITFEBizException	 
	 */
    public void addInfo(IDto IDto) throws ITFEBizException {
    	try {
    		TsIsnopaperDto cbt = (TsIsnopaperDto) IDto;
    		String sbankcodeStr = cbt.getSbankcode();
    		HashMap<String, TsPaybankDto> payBankInfos = SrvCacheFacade.cachePayBankInfo();
    		if(!payBankInfos.containsKey(sbankcodeStr))
    		{
    			throw new RuntimeException("�������д��������֧���кŷ�Χ��");
    		}else
    		{
    			DatabaseFacade.getDb().create(IDto);
    		}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶλ������룬������룬���д��룬ҵ�������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(e.getMessage(), e);
		}
    }

	/**
	 * ɾ��
	 	 
	 * @generated
	 * @param IDto
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto IDto) throws ITFEBizException {
    	try {
			CommonFacade.getODB().deleteRsByDto(IDto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
    }

	/**
	 * �޸�
	 	 
	 * @generated
	 * @param IDto
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto IDto) throws ITFEBizException {
    	try {
    		TsIsnopaperDto cbt = (TsIsnopaperDto) IDto;
    		String sbankcodeStr = cbt.getSbankcode();
    		HashMap<String, TsPaybankDto> payBankInfos = SrvCacheFacade.cachePayBankInfo();
    		if(!payBankInfos.containsKey(sbankcodeStr))
    		{
    			throw new RuntimeException("�������д��������֧���кŷ�Χ��");
    		}else
    		{
    			DatabaseFacade.getDb().update(IDto);
    		}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶλ������룬������룬���д��룬ҵ�������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
    }

}