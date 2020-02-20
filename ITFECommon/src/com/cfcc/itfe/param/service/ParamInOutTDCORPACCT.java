package com.cfcc.itfe.param.service;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdCorpacctDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTDCORPACCT extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator,StringBuffer sb) {
		Log log = LogFactory.getLog(ParamInOutTDCORPACCT.class);
		TdCorpacctDto tsdto = (TdCorpacctDto) dto;
		sb.append(tsdto.getScorpacct()+ separator);
		sb.append(tsdto.getScorpacctname()+ separator);
		sb.append(tsdto.getScrpcorpcode()+ separator);
		sb.append(tsdto.getSopnbnkno()+ separator);
		String opnBnkName = "";
		try {
			opnBnkName = getBnkName(tsdto.getSopnbnkno());
		} catch (Exception e) {
             log.error(e);
		}
		sb.append(opnBnkName);
		sb.append(System.getProperty("line.separator"));
		
	}

	private String getBnkName(String sopnbnkno) throws JAFDatabaseException, ITFEBizException {
		HashMap <String,TsPaybankDto>  map = SrvCacheFacade.cachePayBankInfo();
		if (map != null && !map.isEmpty()) {
			return map.get(sopnbnkno).getSbankname();
		}else{
			return "";
		}
		
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TdCorpacctDto tsdto = (TdCorpacctDto) dto;
		tsdto.setSbookorgcode(cond);
	}
	@Override
	protected IDto createDto(String[] cols,String filename) throws Exception {
		TdCorpacctDto tsdto = new TdCorpacctDto();
		tsdto.setSbookorgcode(cols[0]);
		tsdto.setSoftrecode(cols[1]);
		tsdto.setSdreamno(cols[2]);
		tsdto.setScorpacct(cols[3]);
		tsdto.setScorpacctname(cols[4]);
		tsdto.setScrpcorpcode(cols[5]);
		tsdto.setSbnkno(cols[6]);
		tsdto.setSaddr(cols[7]);
		tsdto.setSopnbnkno(cols[8]);
		tsdto.setCacctattrib(cols[9]);
		if(filename.contains("正常期")){
			tsdto.setCtrimflag("0");
		}else if(filename.contains("调整期")){
			tsdto.setCtrimflag("1");
		}else{
			throw new Exception("文件名不合法！");
		}
		tsdto.setTssysupdate(TSystemFacade.getDBSystemTime());
		return tsdto;
	}

}
