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
	 * 导出TBS文件
	 	 
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
	 * 获取明细信息
	 	 
	 * @generated
	 * @param idto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List getsubinfo(IDto idto) throws ITFEBizException;

	/**
	 * 生成文件批次号
	 	 
	 * @generated
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getTBSNum() throws ITFEBizException;

	/**
	 * 获取收付款人信息
	 	 
	 * @generated
	 * @param paramdto
	 * @return com.cfcc.itfe.persistence.dto.TsPayacctinfoDto
	 * @throws ITFEBizException	 
	 */
   public abstract TsPayacctinfoDto gettspayacctinfo(TvPayoutmsgmainDto paramdto) throws ITFEBizException;

	/**
	 * 获取调拨标志
	 	 
	 * @generated
	 * @param dto
	 * @param subdto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String getmoveflag(TvPayoutmsgmainDto dto, TvPayoutmsgsubDto subdto) throws ITFEBizException;

	/**
	 * 跟新凭证索引表数据状态	 
	 * @generated
	 * @param list
	 * @throws ITFEBizException	 
	 */
   public abstract void updateVdtoStatus(List list) throws ITFEBizException;

}