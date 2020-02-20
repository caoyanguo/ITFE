package com.cfcc.itfe.param.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.param.support.AbstractParamInOut;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class ParamInOutTSTREASURY extends AbstractParamInOut {
	private static Log log = LogFactory.getLog(ParamInOutTSTREASURY.class);

	@Override
	public void createLine(IDto dto, String separator,StringBuffer sb) {
		TsTreasuryDto tredto = (TsTreasuryDto) dto;
		sb.append(tredto.getStrecode() + separator);
		sb.append(tredto.getStrename() + separator);
		//sb.append(getSimpleCode(tredto.getStrecode()) + separator);
		sb.append(tredto.getStrecode().substring(0,2) + separator);
		sb.append(tredto.getStrelevel() + separator);
		sb.append(tredto.getSgoverntrecode() + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("1" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("0" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("" + separator);
		sb.append("0" );
		sb.append(System.getProperty("line.separator"));
	}

	private String getSimpleCode(String strecode) {
		StringBuffer sb = new StringBuffer();
		for (int i = strecode.length(); i > 0; i--) {
			char tmp = strecode.charAt(i-1);
			if ('0' != tmp) {
//				sb.append(tmp);
				sb.append(strecode.substring(0, i));
				break;
			}
		}
		return sb.toString();
	}

	@Override
	protected void setConds(IDto dto, String cond) {
			TsTreasuryDto tsdto = (TsTreasuryDto) dto;
			tsdto.setSorgcode(cond);

	}
	@Override
	protected IDto createDto(String[] cols,String filename) throws Exception {
		TsTreasuryDto tsdto = new TsTreasuryDto();
		tsdto.setSorgcode(cols[0]);
		tsdto.setStrecode(cols[1]);
		tsdto.setStrename(cols[1]);
		tsdto.setStrecodenation(cols[1]);
		tsdto.setStrecodearea(cols[1]);
		tsdto.setStrimflag(cols[1]);
		tsdto.setSpayunitname(cols[1]);
		tsdto.setSfinancename(cols[1]);
		tsdto.setStrelevel(cols[1]);
		tsdto.setStreattrib(cols[1]); 
		tsdto.setSofprovtrecode(cols[1]); 
		tsdto.setSofcitytrecode(cols[1]); 
		tsdto.setSofcountrytrecode(cols[1]); 
		tsdto.setSgoverntrecode(cols[1]); 
		tsdto.setSgoverntrelevel(cols[1]); 
		tsdto.setSisuniontre(cols[1]); 
		tsdto.setCstartzeroacct(cols[1]); 
		tsdto.setImodicount(Integer.valueOf(cols[1])); 
		
		return tsdto;
	}

}
