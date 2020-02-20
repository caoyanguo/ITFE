package com.cfcc.itfe.service.para.tsconvertrea;

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
import com.cfcc.itfe.persistence.dto.TsConvertreaDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsconvertreaService extends IService {



	/**
	 * 添加信息
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(IDto dto) throws ITFEBizException;

	/**
	 * 删除信息
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto dto) throws ITFEBizException;

	/**
	 * 修改信息
	 	 
	 * @generated
	 * @param updateDto
	 * @param oriDto
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(TsConvertreaDto updateDto, TsConvertreaDto oriDto) throws ITFEBizException;

}