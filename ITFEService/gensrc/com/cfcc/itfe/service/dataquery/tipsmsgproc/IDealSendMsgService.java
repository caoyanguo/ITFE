package com.cfcc.itfe.service.dataquery.tipsmsgproc;

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
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.common.page.PageRequest;

/**
 * @author Administrator
 * @time   19-12-08 13:00:40
 * @generated
 * codecomment: 
 */
public interface IDealSendMsgService extends IService {



	/**
	 * ��ҳ��ѯ���ͱ�����Ϣ	 
	 * @generated
	 * @param finddto
	 * @param pageRequest
	 * @param startdate
	 * @param enddate
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public abstract PageResponse findMsgByPage(TvFilepackagerefDto finddto, PageRequest pageRequest, String startdate, String enddate) throws ITFEBizException;

	/**
	 * �ط�������Ϣ	 
	 * @generated
	 * @param senddto
	 * @throws ITFEBizException	 
	 */
   public abstract void sendMsgRepeat(TvFilepackagerefDto senddto) throws ITFEBizException;

	/**
	 * ��ѯ��ӡ���ͱ�����Ϣ	 
	 * @generated
	 * @param printdto
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List findMsgByPrint(TvFilepackagerefDto printdto) throws ITFEBizException;

}