package com.cfcc.itfe.service.para.tsconvertbankname;

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
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsconvertbanknameService extends IService {



	/**
	 * 保存
	 	 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(IDto idto) throws ITFEBizException;

	/**
	 * 删除信息
	 	 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto idto) throws ITFEBizException;

	/**
	 * 信息修改
	 	 
	 * @generated
	 * @param olddto
	 * @param newdto
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(TsConvertbanknameDto olddto, TsConvertbanknameDto newdto) throws ITFEBizException;

}