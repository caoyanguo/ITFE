package com.cfcc.itfe.service.para.tscheckfailreason;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.para.tscheckfailreason.AbstractTsCheckFailReasonService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsCheckfailreasonDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author db2itfe
 * @time 13-09-02 14:46:55 codecomment:
 */

public class TsCheckFailReasonService extends AbstractTsCheckFailReasonService {
	private static Log log = LogFactory.getLog(TsCheckFailReasonService.class);

	/**
	 * ����
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public String save(TsCheckfailreasonDto dto) throws ITFEBizException {
		String msg = "";
		try {
			TsCheckfailreasonDto checkdto = new TsCheckfailreasonDto();
			checkdto.setScheckfailcode(dto.getScheckfailcode());
			checkdto.setSorgcode(dto.getSorgcode());
			List list = CommonFacade.getODB().findRsByDto(checkdto);
			if(list!=null && list.size()>0){
				msg = "ʧ��ԭ���к����������"+dto.getSorgcode()+"+ʧ��ԭ�����"+dto.getScheckfailcode()+"�ظ�������������ʧ��ԭ����룡";
			}else{
				DatabaseFacade.getDb().create(dto);
				msg = "����ɹ���";
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("�ֶ�ʧ��ԭ������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException("�����¼����:" + e.getMessage(), e);
		} catch (ValidateException e) {
			log.error(e);
			e.printStackTrace();
			throw new ITFEBizException("�����¼����:" + e.getMessage(), e);
		}
		return msg;
	}

	/**
	 * ɾ��
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void delete(TsCheckfailreasonDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().delete(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			e.printStackTrace();
			throw new ITFEBizException("ɾ����¼����:" + e.getMessage(), e);
		}
	}

}