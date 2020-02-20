package com.cfcc.itfe.service.expreport;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;

/**
 * @author db2admin
 *
 */

public interface IMakeReport {
	
	/**
	 * 按照业务类型生成导出报表
	 * @param idto
	 * @param bizType
	 * @return
	 * @throws ITFEBizException
	 */
	public abstract String makeReportByBiz(TrIncomedayrptDto idto,String bizType,String sbookorgcode) throws ITFEBizException;
}
