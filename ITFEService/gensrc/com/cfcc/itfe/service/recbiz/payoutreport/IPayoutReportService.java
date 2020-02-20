package com.cfcc.itfe.service.recbiz.payoutreport;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:36
 * @generated
 * codecomment: 
 */
public interface IPayoutReportService extends IService {



	/**
	 * 支出报表生成
	 	 
	 * @generated
	 * @param voucherDtoList
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto payoutVoucherGenerate(List voucherDtoList) throws ITFEBizException;

}