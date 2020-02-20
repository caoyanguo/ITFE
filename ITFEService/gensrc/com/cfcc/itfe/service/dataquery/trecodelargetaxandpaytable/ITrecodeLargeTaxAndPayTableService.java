package com.cfcc.itfe.service.dataquery.trecodelargetaxandpaytable;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface ITrecodeLargeTaxAndPayTableService extends IService {



	/**
	 * ���˰Դ֧��ͳ�Ʊ��ѯ��ҳ
	 	 
	 * @generated
	 * @param queryStyle
	 * @param request
	 * @param IDto
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse queryByPage(String queryStyle, PageRequest request, IDto IDto) throws ITFEBizException;

	/**
	 * ����
	 	 
	 * @generated
	 * @param queryStype
	 * @param list
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportFile(String queryStype, List list) throws ITFEBizException;

	/**
	 * ���˰Դ֧��ͳ�Ʊ��ѯ�б�
	 	 
	 * @generated
	 * @param queryStyle
	 * @param IDto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List queryByList(String queryStyle, IDto IDto) throws ITFEBizException;

}