package com.cfcc.itfe.service.para.tdtaxorgmerger;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITstaxorgcodemergerService extends IService {



	/**
	 * ���
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void add(TdTaxorgMergerDto dto) throws ITFEBizException;

	/**
	 * �޸�
	 	 
	 * @generated
	 * @param dto
	 * @param precode
	 * @param aftercode
	 * @throws ITFEBizException	 
	 */
   public abstract void modify(TdTaxorgMergerDto dto, String precode, String aftercode) throws ITFEBizException;

	/**
	 * ɾ��
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void delete(TdTaxorgMergerDto dto) throws ITFEBizException;

}