package com.cfcc.itfe.voucher.service;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dao.TvVoucherinfoDao;
import com.cfcc.itfe.persistence.dto.TvExceptionmanDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;



public class VoucherException {
	private static Log logger=LogFactory.getLog(VoucherException.class);
	
	public  static void saveErrInfo(String msgno,String err){
		
		saveErrInfo(msgno, new Exception(err));
	}
	public  static void saveErrInfo(String msgno,Exception e){
		logger.error(msgno, e);
		if(msgno==null||msgno.equals("")){
			msgno=MsgConstant.VOUCHER+"_"+MsgConstant.MSG_NO_0000;;
		}
		if(msgno.indexOf(MsgConstant.VOUCHER)<0){
			msgno=MsgConstant.VOUCHER+"_"+msgno;
		}
		
		TvExceptionmanDto tDto=new TvExceptionmanDto();
		tDto.setSexceptioninfo(e.getMessage()==null?(e.toString()):(e.getMessage().length()>1024?e.getMessage().substring(0, 1024):e.getMessage()));
		tDto.setSofbizkind(msgno);
		tDto.setTsupdate(new Timestamp(new java.util.Date().getTime()));
		tDto.setDworkdate(TimeFacade.getCurrentStringTime());
		try {
			DatabaseFacade.getODB().create(tDto);
		} catch (JAFDatabaseException e1) {
			logger.error("记录凭证错误日志出现异常", e1);
		}
	}
}