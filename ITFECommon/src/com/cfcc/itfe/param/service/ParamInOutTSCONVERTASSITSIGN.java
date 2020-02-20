package com.cfcc.itfe.param.service;


import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSCONVERTASSITSIGN extends AbstractParamInOut {


	@Override
	protected IDto createDto(String[] cols,String filename) throws Exception {
		TsConvertassitsignDto tdto=new TsConvertassitsignDto();
		tdto.setSorgcode(cols[0]);
		tdto.setStrecode("N");
		tdto.setStbsassitsign(cols[1]);
		tdto.setSbudgetsubcode("N");
		tdto.setStipsassistsign(cols[2]);
		tdto.setImodicount(0);
		return tdto;
	}

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TsConvertassitsignDto tmp = (TsConvertassitsignDto) dto;
		sb.append(tmp.getSorgcode() + separator);
		sb.append(tmp.getStrecode() + separator);
		sb.append(tmp.getSbudgetsubcode() + separator);
		sb.append(tmp.getStbsassitsign() + separator);
		sb.append(tmp.getStipsassistsign());
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		((TsConvertassitsignDto) dto).setSorgcode(cond);

	}

}
