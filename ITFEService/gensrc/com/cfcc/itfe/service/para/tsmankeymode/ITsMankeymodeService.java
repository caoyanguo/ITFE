package com.cfcc.itfe.service.para.tsmankeymode;

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
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsMankeymodeService extends IService {



	/**
	 * 密钥模式修改
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void keymodeModify(IDto dto) throws ITFEBizException;

	/**
	 * 密钥模式列表
	 	 
	 * @generated
	 * @param dto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse keyList(IDto dto, PageRequest pageRequest) throws ITFEBizException;

}