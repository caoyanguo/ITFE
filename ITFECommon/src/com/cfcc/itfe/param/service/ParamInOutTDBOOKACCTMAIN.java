package com.cfcc.itfe.param.service;

import java.math.BigDecimal;

import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTDBOOKACCTMAIN extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TdBookacctMainDto tsdto = (TdBookacctMainDto) dto;
		sb.append(tsdto.getSbookacct() + separator);
		sb.append(tsdto.getSofbooksbtcode() + separator);
		sb.append(tsdto.getSbookacctname() + separator);
		sb.append(tsdto.getDopenacct().toString().replaceAll("-", "")
				+ separator);
		sb.append(tsdto.getDcancelacct() + separator);
		// 转给广东ets的 3转1 1转0 2不变
		if ("1".equals(tsdto.getCactgenstyle().trim())) {
			sb.append("0" + separator);
		} else if ("3".equals(tsdto.getCactgenstyle().trim())) {
			sb.append("1" + separator);
		} else {
			sb.append(tsdto.getCactgenstyle() + separator);
		}
		sb.append(tsdto.getCbalanceprop() + separator);
		sb.append(tsdto.getCbalancectrl() + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append(tsdto.getCbalanceort() + separator);
		sb.append(tsdto.getCinterestflag() + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		if (tsdto.getDstartinterest() != null)
			sb.append(tsdto.getDstartinterest().toString().replaceAll("-", "")
					+ separator);
		else
			sb.append(tsdto.getDstartinterest() + separator);
		sb.append(tsdto.getSspecialorgcode() + separator);
		sb.append(tsdto.getCwaitreckflag() + separator);
		sb.append(tsdto.getSoftrecode() + separator);
		sb.append("");
		// sb.append(""+ separator);sfdy
		// sb.append("");sfdyfhz
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TdBookacctMainDto tsdto = (TdBookacctMainDto) dto;
		tsdto.setSbookorgcode(cond);
	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TdBookacctMainDto dto = new TdBookacctMainDto();
		dto.setSbookorgcode(cols[0]);
		dto.setSbookacct(cols[1]);
		dto.setSbookacctname(cols[2]);
		dto.setSbookacctsht(cols[3]);
		dto.setSofbooksbtcode(cols[4]);
		dto.setCactgenstyle("1");// 账户生成方式
		if (trim(cols[5]) != null) {
			java.util.Date ddate = TimeFacade.parseDate(cols[5], "yyyy-MM-dd");
			dto.setDopenacct(new java.sql.Date(ddate.getTime()));
		}
		if (trim(cols[6]) != null) {
			java.util.Date cdate = TimeFacade.parseDate(cols[6], "yyyy-MM-dd");
			dto.setDcancelacct(new java.sql.Date(cdate.getTime()));
		}

		dto.setCacctprop(cols[7]);
		dto.setCbookclass("1");// 核算类别
		// 表外标志
		dto.setCbalanceprop(cols[9]);
		dto.setCbalanceort("1");// 余额方向
		dto.setCbalancectrl(cols[10]);

		if (trim(cols[11]) != null) {
			dto.setFpredateswitchbalance(new BigDecimal(cols[11]));
		}
		dto.setCinterestflag(cols[12]);
		if (trim(cols[13]) != null) {
			dto.setIinteresttypeno(Integer.valueOf(cols[13]));
		}
		if (trim(cols[14]) != null) {
			java.util.Date idate = TimeFacade.parseDate(cols[14], "yyyy-MM-dd");
			dto.setDstartinterest(new java.sql.Date(idate.getTime()));
		}
		dto.setSspecialorgcode(trim(cols[15]));
		dto.setSoftrecode(trim(cols[16]));
		dto.setCwaitreckflag(cols[17]);
		dto.setTssysupdate(TSystemFacade.getDBSystemTime());
		return dto;
	}

}
