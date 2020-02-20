package com.cfcc.itfe.param.service;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectAdjustDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSBUDGETSUBJECT extends AbstractParamInOut {

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		if (dto instanceof TsBudgetsubjectDto) {
			TsBudgetsubjectDto tsdto = (TsBudgetsubjectDto) dto;
			sb.append(tsdto.getSsubjectcode() + separator);
			sb.append(tsdto.getSsubjectname() + separator);
			sb.append(tsdto.getSsubjecttype() + separator);
			sb.append(tsdto.getSclassflag() + separator);
			sb.append(tsdto.getSsubjectclass() + separator);
			sb.append(tsdto.getSwriteflag() + separator);
			sb.append(tsdto.getSsubjectattr() + separator);
			String smoveflag = "";
			if (!"0".equals(tsdto.getSmoveflag())) {// TCBS调拨标志1调拨0非调拨 TBS 1-调拨、2-非调拨
				smoveflag = "1";
			} else {
				smoveflag = "2";
			}
			sb.append(smoveflag + separator);
			sb.append("" + separator);
			sb.append("" + separator);
			sb.append("" + separator);
			sb.append("" + separator);
			sb.append(tsdto.getSsubjectcode() + separator);
			sb.append("" + separator);
			sb.append("");
			sb.append(System.getProperty("line.separator"));
		} else if (dto instanceof TsBudgetsubjectAdjustDto) {
			TsBudgetsubjectAdjustDto tsdto = (TsBudgetsubjectAdjustDto) dto;
			sb.append(tsdto.getSsubjectcode() + separator);
			sb.append(tsdto.getSsubjectname() + separator);
			sb.append(tsdto.getSsubjecttype() + separator);
			sb.append(tsdto.getSclassflag() + separator);
			sb.append(tsdto.getSsubjectclass() + separator);
			sb.append(tsdto.getSwriteflag() + separator);
			sb.append(tsdto.getSsubjectattr() + separator);
			String smoveflag = "";
			if (!"0".equals(tsdto.getSmoveflag())) {// TCBS调拨标志1调拨0非调拨 TBS 1-调拨、2-非调拨
				smoveflag = "1";
			} else {
				smoveflag = "2";
			}
			sb.append(smoveflag + separator);
			sb.append("" + separator);
			sb.append("" + separator);
			sb.append("" + separator);
			sb.append("" + separator);
			sb.append(tsdto.getSsubjectcode() + separator);
			sb.append("" + separator);
			sb.append("");
			sb.append(System.getProperty("line.separator"));
		}

	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TsBudgetsubjectDto tsdto = (TsBudgetsubjectDto) dto;
		tsdto.setSorgcode(cond);
	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		throw new ITFEBizException("不支持此接口");
	}

}
