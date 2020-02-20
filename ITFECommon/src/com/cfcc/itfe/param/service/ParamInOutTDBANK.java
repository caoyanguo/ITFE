package com.cfcc.itfe.param.service;

import java.util.Map;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdBankDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTDBANK extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TdBankDto tsdto = (TdBankDto) dto;
		String bnkcode = tsdto.getSbnkcode();
		sb.append(bnkcode + separator);
		sb.append(ensureValidStr(tsdto.getSbnkname()) + separator);
		sb.append(getShortCode(bnkcode) + separator);
		sb.append(tsdto.getSofbnkflag() + separator);
		sb.append(tsdto.getScityexchgno() + separator);
		sb.append(getBnkClass(tsdto.getSofbnkflag()) + separator);
		sb.append("");
		sb.append(System.getProperty("line.separator"));
	}

	private String getShortCode(String bnkcode) {
		String shcode = "";
		if (bnkcode != null && !"".equals(bnkcode) && bnkcode.length() >= 3) {
			shcode = bnkcode.substring(0, 3);
		}
		return shcode;
	}
	
	/**
	 * 将传入的字符串去掉空格、逗号等字符
	 * 
	 * @param oriStr
	 * @return
	 */
	private String ensureValidStr(String oriStr) {
		if(null != oriStr && !"".equals(oriStr)) {
			return oriStr.replaceAll(" ", "").replaceAll("	", "").replaceAll(",", "").replaceAll("，", "");
		} else {
			return "";
		}
	}

	private String getBnkClass(String sofbnkflag) {
		if (sofbnkflag == null || "".equals(sofbnkflag)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(sofbnkflag);
		for (int i = 0; i < 12 - sofbnkflag.length(); i++) {
			sb.append(sofbnkflag.charAt(0));
		}
		return sb.toString();
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TdBankDto tsdto = (TdBankDto) dto;
		tsdto.setSbookorgcode(cond);
	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TdBankDto dto = new TdBankDto();
//		dto.setIseqno( Long.valueOf(SequenceGenerator.getNextByDb2("STAMP_FILE_SEQUENCE", SequenceName.TRAID_SEQ_CACHE, SequenceName.TRAID_SEQ_STARTWITH)));
		dto.setSbookorgcode(cols[0]);
		dto.setSbnkcode(cols[1]);
		Map<String, TsPaybankDto> bankmap = SrvCacheFacade.cachePayBankInfo();
		if (null != bankmap.get(cols[1])) {
			dto.setSbnkname(bankmap.get(cols[1]).getSbankname());
		}else{
			dto.setSbnkname("");
		}

		dto.setSofbnkflag("");
		dto.setScityexchgno(cols[2]);
		dto.setTssysupdate(TSystemFacade.getDBSystemTime());

		return dto;
	}

}
