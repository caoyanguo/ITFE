package com.cfcc.itfe.param.service;

import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsDrawbackreasonDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSDRAWBACKREASON extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator,StringBuffer sb) {
		TsDrawbackreasonDto tsdto = (TsDrawbackreasonDto) dto;
		sb.append(tsdto.getStbsdrawcode() + separator);
		sb.append(tsdto.getStbsdrawname() );
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TsDrawbackreasonDto tsdto = (TsDrawbackreasonDto) dto;
		tsdto.setSbookorgcode(cond);
	}
	@Override
	protected IDto createDto(String[] cols,String filename) throws Exception {
		TsDrawbackreasonDto tsdto = new TsDrawbackreasonDto();
		tsdto.setSbookorgcode(cols[0]);
		tsdto.setStbsdrawcode(cols[1]);
		tsdto.setStbsdrawname(cols[2]);
		tsdto.setStcbsdrawcode(cols[3]);
		tsdto.setStcbsdrawname(cols[4]);
		tsdto.setTssysupdate(TSystemFacade.getDBSystemTime());
		
		return tsdto;
	}

}
