package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface IFileResolveCommonService extends IService {



	/**
	 * 解析文件
	 	 
	 * @generated
	 * @param filelist
	 * @param biztype
	 * @param filekind
	 * @param paramdto
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
   public abstract MulitTableDto loadFile(List filelist, String biztype, String filekind, IDto paramdto) throws ITFEBizException;

	/**
	 * createFileDto
	 	 
	 * @generated
	 * @param multidto
	 * @throws ITFEBizException	 
	 */
   public abstract void createFileDto(MulitTableDto multidto) throws ITFEBizException;

	/**
	 * 更新资金报文信息
	 	 
	 * @generated
	 * @param payreckbackdto
	 * @throws ITFEBizException	 
	 */
   public abstract void updatefundinfo(TvPayreckBankBackDto payreckbackdto) throws ITFEBizException;

}