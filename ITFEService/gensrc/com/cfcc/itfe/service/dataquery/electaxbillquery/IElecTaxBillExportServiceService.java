package com.cfcc.itfe.service.dataquery.electaxbillquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IElecTaxBillExportServiceService extends IService {



	/**
	 * ��ѯ��������˰Ʊ
	 	 
	 * @generated
	 * @param idto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportfile(IDto idto) throws ITFEBizException;

	/**
	 * ҵ����ͳ��
	 	 
	 * @generated
	 * @param paramMap
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List busworkcount(Map paramMap) throws ITFEBizException;

}