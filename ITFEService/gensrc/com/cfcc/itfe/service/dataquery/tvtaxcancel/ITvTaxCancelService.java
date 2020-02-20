package com.cfcc.itfe.service.dataquery.tvtaxcancel;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface ITvTaxCancelService extends IService {



	/**
	 * ≤È—Ø	 
	 * @generated
	 * @param dtoInfo
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List query(TvTaxCancelDto dtoInfo) throws ITFEBizException;

}