package com.cfcc.itfe.param.service;

import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsTreAcctNoBnkCodeDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSTREACCTNOBNKCODE extends AbstractParamInOut {

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TsTreAcctNoBnkCodeDto treAcctNoBnkCodeDto = new TsTreAcctNoBnkCodeDto();
		treAcctNoBnkCodeDto.setSorgcode(cols[0]);
		treAcctNoBnkCodeDto.setSpaybankno(cols[1]);
		treAcctNoBnkCodeDto.setSbookacct(cols[2]);
		treAcctNoBnkCodeDto.setSbookacctname(cols[3]);
		
		return treAcctNoBnkCodeDto;
	}

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TsTreAcctNoBnkCodeDto treAcctNoBnkCodeDto = (TsTreAcctNoBnkCodeDto)dto;
		sb.append(treAcctNoBnkCodeDto.getSorgcode() + separator);
		sb.append(treAcctNoBnkCodeDto.getSpaybankno() + separator);
		sb.append(treAcctNoBnkCodeDto.getSbookacct() + separator);
		sb.append(treAcctNoBnkCodeDto.getSbookacctname() );
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TsTreAcctNoBnkCodeDto treAcctNoBnkCodeDto = (TsTreAcctNoBnkCodeDto)dto;
		treAcctNoBnkCodeDto.setSorgcode(cond);
	}

}
