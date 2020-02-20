package com.cfcc.itfe.param.service;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdBooksbtDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTDBOOKSBT extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TdBooksbtDto tsdto = (TdBooksbtDto) dto;
		sb.append(tsdto.getSbooksbtcode() + separator);
		sb.append(tsdto.getSbooksbtname() + separator);
		//根据广东要求 将会计科目类型 由4转成6
		if (StringUtils.isNotBlank(tsdto.getCbooksbttype())
				&& "4".equals(tsdto.getCbooksbttype().trim())) {
			sb.append("6" + separator);
		} else {
			sb.append(tsdto.getCbooksbttype() + separator);
		}
		sb.append(tsdto.getCbalanceprop() + separator);
		sb.append("0" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("0" + separator);
		sb.append(tsdto.getIprintno() + separator);
		sb.append(tsdto.getCstockflag() + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TdBooksbtDto tsdto = (TdBooksbtDto) dto;
		tsdto.setSbookorgcode(cond);
	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TdBooksbtDto tsdto = new TdBooksbtDto();
		tsdto.setSbookorgcode(cols[0]);
		tsdto.setSbooksbtcode(cols[1]);
		tsdto.setSbooksbtname(cols[2]);
		tsdto.setCbooksbttype(cols[3]);
		tsdto.setCbalanceprop(cols[4]);
		tsdto.setCstockflag(cols[5]);
		if (trim(cols[6]) != null)
			tsdto.setIprintno(Integer.valueOf(cols[6]));
		tsdto.setTssysupdate(TSystemFacade.getDBSystemTime());
		return tsdto;
	}

}
