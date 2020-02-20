package com.cfcc.itfe.service.dataquery.directpayquery;

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
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgmainDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IDirectPayService extends IService {



	/**
	 * ��ҳ��ѯֱ��֧���������Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPage(TvDirectpaymsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * ��ҳ��ѯֱ��֧���������Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPage(TvDirectpaymsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * �ط�TIPSû���յ��ı���	 
	 * @generated
	 * @param scommitdate
	 * @param spackageno
	 * @param sorgcode
	 * @param sfilename
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String reSendMsg(String scommitdate, String spackageno, String sorgcode, String sfilename) throws ITFEBizException;

	/**
	 * ��ҳ��ѯֱ��֧���������Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMainByPageForHis(HtvDirectpaymsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * ��ҳ��ѯֱ��֧���������Ϣ	 
	 * @generated
	 * @param mainDto
	 * @param pageRequest
	 * @param expfunccode
	 * @param payamt
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findSubByPageForHis(HtvDirectpaymsgmainDto mainDto, PageRequest pageRequest, String expfunccode, String payamt) throws ITFEBizException;

	/**
	 * ����txt	 
	 * @generated
	 * @param mainDto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String dataexport(IDto mainDto, String selectedtable) throws ITFEBizException;

	/**
	 * ����CSV	 
	 * @generated
	 * @param mainDto
	 * @param selectedtable
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String exportFile(IDto mainDto, String selectedtable) throws ITFEBizException;

}