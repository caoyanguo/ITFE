package com.cfcc.itfe.service.sendbiz.exporttbsfiletxt;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface IExportTBSfiletxtService extends IService {



	/**
	 * ����TBS�ļ�
	 	 
	 * @generated
	 * @param sbiztypetbs
	 * @param orgcode
	 * @param dacctdate
	 * @param ctrimflag
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List exportTBSdata(String sbiztypetbs, String orgcode, Date dacctdate, String ctrimflag) throws ITFEBizException;

	/**
	 * ��ȡ��ϸ��Ϣ
	 	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getsubinfo(IDto idto) throws ITFEBizException;

	/**
	 * �����ļ����κ�
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getTBSNum() throws ITFEBizException;

	/**
	 * ��ȡ�ո�������Ϣ
	 	 
	 * @generated
	 * @param paramdto
	 * @return com.cfcc.itfe.persistence.dto.TsPayacctinfoDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsPayacctinfoDto gettspayacctinfo(TvPayoutmsgmainDto paramdto) throws ITFEBizException;

	/**
	 * ��ȡ������־
	 	 
	 * @generated
	 * @param dto
	 * @param subdto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getmoveflag(TvPayoutmsgmainDto dto, TvPayoutmsgsubDto subdto) throws ITFEBizException;

	/**
	 * ����ƾ֤����������״̬	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void updateVdtoStatus(List list) throws ITFEBizException;

}