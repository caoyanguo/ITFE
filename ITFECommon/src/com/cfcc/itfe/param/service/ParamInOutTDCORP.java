package com.cfcc.itfe.param.service;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTDCORP extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator,StringBuffer sb) {
		TdCorpDto tsdto = (TdCorpDto) dto;
		sb.append(tsdto.getScorpcode() + separator);
		sb.append(tsdto.getScorpname() + separator);
		sb.append(tsdto.getScorpsht() + separator);
		sb.append(tsdto.getCtaxpayerprop());
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TdCorpDto tsdto = (TdCorpDto) dto;
		tsdto.setSbookorgcode(cond);
	}
	@Override
	protected IDto createDto(String[] cols,String filename) throws Exception {
		throw new ITFEBizException("不支持的接口");
	}

}
