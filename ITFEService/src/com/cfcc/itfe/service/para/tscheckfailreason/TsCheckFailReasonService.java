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
	 * 保存
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
				msg = "失败原因中核算主体代码"+dto.getSorgcode()+"+失败原因代码"+dto.getScheckfailcode()+"重复，请重新输入失败原因代码！";
			}else{
				DatabaseFacade.getDb().create(dto);
				msg = "保存成功！";
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段失败原因代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException("保存记录出错:" + e.getMessage(), e);
		} catch (ValidateException e) {
			log.error(e);
			e.printStackTrace();
			throw new ITFEBizException("保存记录出错:" + e.getMessage(), e);
		}
		return msg;
	}

	/**
	 * 删除
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
			throw new ITFEBizException("删除记录出错:" + e.getMessage(), e);
		}
	}

}