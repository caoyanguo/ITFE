package com.cfcc.itfe.param.service;

import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsCorrreasonDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSCORRREASON extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator,StringBuffer sb) {
		TsCorrreasonDto tsdto = (TsCorrreasonDto) dto;
		sb.append(tsdto.getStbscorrcode() + separator);
		sb.append(tsdto.getStbscorrname() );
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TsCorrreasonDto tsdto = (TsCorrreasonDto) dto;
		tsdto.setSbookorgcode(cond);
	}
	@Override
	protected IDto createDto(String[] cols,String filename) throws Exception {
		TsCorrreasonDto tsdto = new TsCorrreasonDto();
		tsdto.setSbookorgcode(cols[0]);
		tsdto.setStbscorrcode(cols[1]);
		tsdto.setStbscorrname(cols[2]);
		tsdto.setStcbscorrcode(cols[3]);
		tsdto.setStcbscorrname(cols[4]);
		tsdto.setTssysupdate(TSystemFacade.getDBSystemTime());
		
		return tsdto;
	}

}
