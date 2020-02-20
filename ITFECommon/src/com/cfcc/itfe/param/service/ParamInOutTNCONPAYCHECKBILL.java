package com.cfcc.itfe.param.service;

import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTNCONPAYCHECKBILL extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator,StringBuffer sb) {
		TnConpaycheckbillDto tsdto = (TnConpaycheckbillDto) dto;
		sb.append(tsdto.getStrecode()+ separator);
		sb.append(tsdto.getSbdgorgcode()+ separator);
		sb.append(tsdto.getSbdgorgname()+ separator);
		sb.append(tsdto.getSbnkno()+ separator);
		sb.append(tsdto.getSfuncsbtcode()+ separator);
		sb.append(tsdto.getSecosbtcode()+ separator);
		sb.append(tsdto.getSecosbtcode()+ separator);//∂‘’ÀƒÍ‘¬
		sb.append(tsdto.getFlastmonthzeroamt()+ separator);
		sb.append(tsdto.getFcurzeroamt()+ separator);
		sb.append(tsdto.getFcurreckzeroamt()+ separator);
		sb.append(tsdto.getFlastmonthsmallamt()+ separator);
		sb.append(tsdto.getFcursmallamt()+ separator);
		sb.append(tsdto.getFcurrecksmallamt()+ separator);
		sb.append(tsdto.getCamtkind()+ separator);
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TnConpaycheckbillDto tsdto = (TnConpaycheckbillDto) dto;
		tsdto.setSbookorgcode(cond);
	}
	@Override
	protected IDto createDto(String[] cols,String filename) throws Exception {
		TnConpaycheckbillDto tsdto = new TnConpaycheckbillDto();
		tsdto.setSbookorgcode(cols[0]);
		tsdto.setStrecode(cols[0]);
		tsdto.setSbdgorgcode(cols[0]);
		tsdto.setSbdgorgname(cols[0]);
		return tsdto;
	}

}
