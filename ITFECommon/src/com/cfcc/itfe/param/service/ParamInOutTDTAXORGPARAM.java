package com.cfcc.itfe.param.service;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class ParamInOutTDTAXORGPARAM extends AbstractParamInOut {
	private static Log log = LogFactory.getLog(ParamInOutTDTAXORGPARAM.class);

	public static boolean isfirst = false; // 判断是否是第一次处理

	@Override
	protected void createLine(IDto dto, String separator, StringBuffer sb) {
		TdTaxorgParamDto tsdto = (TdTaxorgParamDto) dto;
		TsConverttaxorgDto tax = null;
		// 如果TD_TAXORG_PARAM，则将TS_TAXORG中征收机关代码不在TD_TAXORG_PARAM内的也导出
		try {
			if (isfirst) {
				isfirst = false;
				String sql = "SELECT  a.* FROM TS_TAXORG a WHERE a.S_TAXORGCODE NOT IN (SELECT S_TAXORGCODE FROM TD_TAXORG_PARAM WHERE S_BOOKORGCODE = ? ) AND a.s_orgcode = ? ";
				SQLExecutor sqlexec;
				sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				sqlexec.addParam(tsdto.getSbookorgcode());
				sqlexec.addParam(tsdto.getSbookorgcode());
				List<TsTaxorgDto> taxlist = (List<TsTaxorgDto>) sqlexec
						.runQueryCloseCon(sql, TsTaxorgDto.class)
						.getDtoCollection();
				if (null != taxlist || taxlist.size() == 0) {
					for (TsTaxorgDto tmptaxorg : taxlist) {
						tax = getTax(tmptaxorg.getStaxorgcode().trim(),tsdto);
						if (tax == null) {
							sb.append("error:未找到征收机关代码为" + tmptaxorg.getStaxorgcode()
									+ "对应的地方横连征收机关代码和国库代码，请在征收机关对照维护中维护参数后重新导出！");
							return;
						}
						sb.append(tax.getStrecode() + separator);
						sb.append(tax.getStbstaxorgcode() + separator);
						sb.append(tsdto.getStaxorgname() + separator);
						sb.append(tmptaxorg.getStaxprop().trim() + separator);
						sb.append("" + separator);
						sb.append(tsdto.getStaxorgsht() + separator);
						sb.append("1");
						sb.append(System.getProperty("line.separator"));
					}
				}
			}

			String code = tsdto.getStaxorgcode();

			tax = getTax(code,tsdto);
			if (tax == null) {
				sb.append("error:未找到征收机关代码为" + code
						+ "对应的地方横连征收机关代码和国库代码，请在征收机关对照维护中维护参数后重新导出！");
				return;
			}
		} catch (Exception e) {
			log.error(e);
		}
		sb.append(tax.getStrecode() + separator);
		sb.append(tax.getStbstaxorgcode() + separator);
		sb.append(tsdto.getStaxorgname() + separator);
		sb.append(tsdto.getCtaxorgprop() + separator);
		sb.append(tsdto.getStaxorgphone() + separator);
		sb.append(tsdto.getStaxorgsht() + separator);
		sb.append("1");
		sb.append(System.getProperty("line.separator"));
	}

	private TsConverttaxorgDto getTax(String code,TdTaxorgParamDto tsdto) throws JAFDatabaseException,
			ITFEBizException {
		HashMap<String, TsConverttaxorgDto> mapTaxInfo = SrvCacheFacade
				.cacheTaxInfo(null);
		for (TsConverttaxorgDto tax : mapTaxInfo.values()) {
			if (tax.getStcbstaxorgcode().equals(code) && tax.getSorgcode().equals(tsdto.getSbookorgcode())) {
				return tax;
			}
		}
		return null;
	}

	@Override
	protected void setConds(IDto dto, String cond) {
		TdTaxorgParamDto tsdto = (TdTaxorgParamDto) dto;
		tsdto.setSbookorgcode(cond);

	}

	@Override
	protected IDto createDto(String[] cols, String filename) throws Exception {
		TdTaxorgParamDto dto = new TdTaxorgParamDto();
		dto.setSbookorgcode(cols[0]);
		dto.setStaxorgcode(cols[1]);
		if (filename.contains("正常期")) {
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
