package com.cfcc.itfe.service.recbiz.banknameenter;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.service.recbiz.banknameenter.AbstractBankNameEnterService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author Administrator
 * @time   13-05-22 13:07:12
 * codecomment: 
 */

public class BankNameEnterService extends AbstractBankNameEnterService {
	private static Log log = LogFactory.getLog(BankNameEnterService.class);

	public Void save(IDto dto) throws ITFEBizException {
		TsConvertbanknameDto convertbankdto = (TsConvertbanknameDto)dto;
		try {
			TsConvertbanknameDto tempdto = new TsConvertbanknameDto();
			tempdto.setSorgcode(convertbankdto.getSorgcode());
			tempdto.setSbankname(convertbankdto.getSbankname());
			List list = CommonFacade.getODB().findRsByDto(tempdto);
			if(list.size()==0){
				DatabaseFacade.getODB().create(convertbankdto);
			}else{
				DatabaseFacade.getODB().update(convertbankdto);
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("插入行名对照表数据出错", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("查询行名对照表数据出错", e);
		}
		return null;
	}

	public List findRsByDtoAndWhere(String wheresql, IDto dto)
			throws ITFEBizException {
		try {
			return CommonFacade.getODB().findRsByDtoForWhere(dto, wheresql);
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}