package com.cfcc.itfe.service.para.tsconvertax;

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
import com.cfcc.itfe.persistence.dto.TsConvertaxDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsconvertaxService extends IService {



	/**
	 * �����Ϣ
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(IDto dto) throws ITFEBizException;

	/**
	 * �޸���Ϣ
	 	 
	 * @generated
	 * @param nowdto
	 * @param oridto
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(TsConvertaxDto nowdto, TsConvertaxDto oridto) throws ITFEBizException;

	/**
	 * ɾ��dto��Ϣ
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto dto) throws ITFEBizException;

}