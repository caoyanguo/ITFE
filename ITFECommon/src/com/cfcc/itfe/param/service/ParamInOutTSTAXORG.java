package com.cfcc.itfe.param.service;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSTAXORG extends AbstractParamInOut {
	private static Log log = LogFactory.getLog(ParamInOutTSTAXORG.class);

	public static boolean isfirst = false; // �ж��Ƿ��ǵ�һ�δ���

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		try {
			TsTaxorgDto tsdto = (TsTaxorgDto) dto;
			TsConverttaxorgDto tax = null;
			String taxOrgCode = tsdto.getStaxorgcode();

			tax = getTax(taxOrgCode,tsdto);
			if (tax == null) {
				sb.append("error:δ�ҵ����ջ��ش���Ϊ" + taxOrgCode
						+ "��Ӧ�ĵط��������ջ��ش���͹�����룬�������ջ��ض���ά����ά�����������µ�����");
				return;
			}
			sb.append(tax.getStbstaxorgcode() + separator);
			sb.append(tax.getStrecode() + separator);
			sb.append(tsdto.getStaxprop() + separator);
			sb.append(tsdto.getStaxorgname() + separator);
			sb.append("" + separator); // ���ջ��ص绰
			sb.append("" + separator); //����
			sb.append("1");
			sb.append(System.getProperty("line.separator"));
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		
	}

	private TsConverttaxorgDto getTax(String taxOrgCode,TsTaxorgDto tsdto) throws JAFDatabaseException,
			ITFEBizException {
		HashMap<String, TsConverttaxorgDto> mapTaxInfo = SrvCacheFacade
				.cacheTaxInfo(null);
		for (TsConverttaxorgDto tax : mapTaxInfo.values()) {
			if (tax.getStcbstaxorgcode().equals(taxOrgCode) && tax.getStrecode().equals(tsdto.getStrecode()) && tax.getSorgcode().equals(tsdto.getSorgcode())) {
				return tax;
			}
		}
		return null;
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TsTaxorgDto tsdto = (TsTaxorgDto) dto;
		tsdto.setSorgcode(cond);

	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TdTaxorgParamDto dto = new TdTaxorgParamDto();
		dto.setSbookorgcode(cols[0]);
		dto.setStaxorgcode(cols[1]);
		if (filename.contains("������")) {
			dto.setCtrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);
		} else {
			dto.setCtrimflag(StateConstant.TRIMSIGN_FLAG_TRIM);
		}
		dto.setStaxorgname(cols[2]);
		dto.setCtaxorgprop(cols[3]);
		dto.setStaxorgphone(cols[4]);
		dto.setTssysupdate(TSystemFacade.getDBSystemTime());

		return dto;
	}
}
