package com.cfcc.itfe.param.service;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 征收机关对照关系参数(满足广东需求)
 * 
 * @author hua
 * @time 2016-10-20
 */
public class ParamInOutTSCONVERTTAXORG extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TsConverttaxorgDto tsdto = (TsConverttaxorgDto) dto;
		sb.append(tsdto.getStrecode() + separator);
		sb.append(tsdto.getStcbstaxorgcode() + separator);
		sb.append(tsdto.getStbstaxorgcode() + separator);
		sb.append("" + separator);
		sb.append(tsdto.getImodicount());
		sb.append("");
		sb.append(System.getProperty("line.separator"));
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TsConverttaxorgDto tsdto = (TsConverttaxorgDto) dto;
		tsdto.setSorgcode(cond);
	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TsConverttaxorgDto dto = new TsConverttaxorgDto();
		Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
		String trecode = cols[0];
		if (StringUtils.isBlank(trecode)) {
			throw new ITFEBizException("国库代码不能为空!");
		}
		String orgcode = treasuryInfo.get(trecode).getSorgcode();
		dto.setSorgcode(orgcode);
		String tcbsTaxOrgCode = cols[1];
		if (StringUtils.isBlank(tcbsTaxOrgCode)) {
			throw new ITFEBizException("TIPS征收机关代码不能为空!");
		}
		dto.setStcbstaxorgcode(tcbsTaxOrgCode);
		dto.setSnationtaxorgcode("N");
		dto.setSareataxorgcode("N");
		String tbsTaxOrgCode = cols[2];
		if (StringUtils.isBlank(tbsTaxOrgCode)) {
			throw new ITFEBizException("横连征收机关代码不能为空!");
		}
		dto.setStbstaxorgcode(tbsTaxOrgCode);
		dto.setStrecode(trecode);
		dto.setImodicount(Integer.valueOf(cols[4]));
		return dto;
	}

}
