package com.cfcc.itfe.service.dataquery.tvfinincomeonlinedetailblend;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvIncomeonlineIncomeondetailBlendDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.exception.ITFEBizException;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface ITvFinIncomeonlineDetailBlendService extends IService {



	/**
	 * ��ѯ����˰Ʊ�����ˮ
	 	 
	 * @generated
	 * @param finddto
	 * @param parammap
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
   public abstract Map searchBlend(TvIncomeonlineIncomeondetailBlendDto finddto, Map parammap) throws ITFEBizException;

	/**
	 * �������ݱ���
	 	 
	 * @generated
	 * @param idtodata
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String adddata(TvIncomeonlineIncomeondetailBlendDto idtodata) throws ITFEBizException;

	/**
	 * �������
	 	 
	 * @generated
	 * @param finddto
	 * @param paramMap
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String blendStorage(TvIncomeonlineIncomeondetailBlendDto finddto, Map paramMap) throws ITFEBizException;

	/**
	 * ���������ܱ���
	 	 
	 * @generated
	 * @param idto
	 * @param strecode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findSummaryReport(IDto idto, String strecode) throws ITFEBizException;

}