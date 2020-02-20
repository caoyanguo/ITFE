package com.cfcc.itfe.param.service;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSPAYBANK extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TsPaybankDto tsdto = (TsPaybankDto) dto;
		sb.append(tsdto.getSbankno() + separator);
		sb.append(tsdto.getSstate() + separator);
		sb.append(tsdto.getCbnkclass() + separator);
		sb.append(tsdto.getCbnkclsno() + separator);

		sb.append(tsdto.getSpaybankno() + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append(tsdto.getSofcity() + separator);
		sb.append(tsdto.getCbnkacctsta() + separator);
		sb.append("" + separator);
		sb.append("" + separator);

		sb.append(ensureValidStr(tsdto.getSbankname()) + separator);// ������ȫ��
		sb.append("" + separator);

		sb.append(ensureValidStr(tsdto.getSbnkaddr()) + separator);
		sb.append(tsdto.getCbnkpostcode() + separator);
		sb.append(ensureValidStr(tsdto.getSbnktel()) + separator);
		sb.append("" + separator);
		sb.append(tsdto.getDaffdate().toString().replaceAll("-", "") + separator);
		sb.append("" + separator);
		sb.append(tsdto.getDaffdate().toString().replaceAll("-", "") + separator);
		sb.append(tsdto.getCopttype() + separator);
		sb.append("" + separator);
		sb.append("");

		sb.append(System.getProperty("line.separator"));
	}

	/**
	 * ��������ַ���ȥ���ո񡢶��ŵ��ַ�
	 * 
	 * @param oriStr
	 * @return
	 */
	private String ensureValidStr(String oriStr) {
		if(null != oriStr && !"".equals(oriStr)) {
			return oriStr.replaceAll(" ", "").replaceAll("	", "").replaceAll(",", "").replaceAll("��", "");
		} else {
			return "";
		}
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TsPaybankDto tsdto = (TsPaybankDto) dto;
		// tsdto.setSorgcode(cond);
	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		throw new ITFEBizException("��֧��������TXT�ӿ�,�뵼��XML�ļ�");
	}

}
