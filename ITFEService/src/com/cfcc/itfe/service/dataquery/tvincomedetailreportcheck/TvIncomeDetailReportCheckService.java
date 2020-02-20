package com.cfcc.itfe.service.dataquery.tvincomedetailreportcheck;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvIncomeDetailReportCheckDto;
import com.cfcc.itfe.time.service.IncomeDetailReportCheck;

/**
 * �����ˮ�뱨��˶�
 * @author hjr
 * @time   14-02-26 15:49:44
 * codecomment: 
 */

public class TvIncomeDetailReportCheckService extends AbstractTvIncomeDetailReportCheckService {
	private static Log log = LogFactory.getLog(TvIncomeDetailReportCheckService.class);	
	/**
	 * �˶������ˮ�ܽ���뱨���ܽ���Ƿ����	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Integer
	 * @throws ITFEBizException	 
	 */
    public String incomeDetailReportCheck(TvIncomeDetailReportCheckDto dto) throws ITFEBizException {
    	return new IncomeDetailReportCheck().incomeDetailReportCheck(dto);
    }
}