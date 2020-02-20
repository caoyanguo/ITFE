package com.cfcc.itfe.service.para.tsfinmovepaysub;

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
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsFinmovepaysubService extends IService {



	/**
	 * 财政调拨支出列表
	 	 
	 * @generated
	 * @param pageRequest
	 * @param dto
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse finmovepayList(PageRequest pageRequest, IDto dto) throws ITFEBizException;

	/**
	 * 财政调拨支出删除
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void finmovepayDelete(IDto dto) throws ITFEBizException;

	/**
	 * 财政调拨支出录入
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void finmovepaySave(IDto dto) throws ITFEBizException;

	/**
	 * 财政调拨支出修改
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void finmovepayModify(IDto dto) throws ITFEBizException;

}