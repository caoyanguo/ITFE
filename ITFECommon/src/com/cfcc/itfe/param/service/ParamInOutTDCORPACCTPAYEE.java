package com.cfcc.itfe.param.service;

import java.util.List;

import com.cfcc.itfe.constant.ParamConstant;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdCorpacctDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTDCORPACCTPAYEE extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator,StringBuffer sb) {
		TdCorpacctDto tsdto = (TdCorpacctDto) dto;
		sb.append("1"+ separator);
		sb.append(tsdto.getScrpcorpcode()+ separator);
		sb.append(tsdto.getSbnkno()+ separator);
		sb.append(tsdto.getSbnkno()+ separator);
		sb.append(""+ separator);
		sb.append(tsdto.getSopnbnkno()+ separator);
		sb.append(tsdto.getScorpacct());
		sb.append(""+separator);
		sb.append("");
		sb.append(System.getProperty("line.separator"));
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
		tsdto.setSoftrecode(cols[0]);
		tsdto.setSdreamno(cols[0]);
		tsdto.setCtrimflag(cols[0]);
		tsdto.setScorpacct(cols[0]);
		tsdto.setScorpacctname(cols[0]);
		tsdto.setScrpcorpcode(cols[0]);
		tsdto.setSbnkno(cols[0]);
		tsdto.setSopnbnkno(cols[0]);
		tsdto.setSaddr(cols[0]);
		tsdto.setCacctattrib(cols[0]);
		tsdto.setTssysupdate(TSystemFacade.getDBSystemTime());
		return tsdto;
	}

	@Override
	protected List<IDto> getDtos(String tabcode, String orgcode)
			throws Exception {
		IDto _dto = new TdCorpacctDto();
		if (!ParamConstant.CENTERORG.equals(orgcode)) {
			setConds(_dto,orgcode);
		}
		return CommonFacade.getODB().findRsByDto(_dto);
	}

}
