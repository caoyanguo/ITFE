package com.cfcc.itfe.service.para.tsstampposition;

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

/**
 * @author Administrator
 * @time   19-12-08 13:00:39
 * @generated
 * codecomment: 
 */
public interface ITsstamppositionService extends IService {



	/**
	 * ����
	 	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void addInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * ɾ��
	 	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void delInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * �޸�
	 	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
   public abstract void modInfo(IDto dtoInfo) throws ITFEBizException;

	/**
	 * ��ѯǩ��λ����Ϣ
	 	 
	 * @generated
	 * @param certID
	 * @param treCode
	 * @param stYear
	 * @param vtCode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
   public abstract List queryStampPosition(String certID, String treCode, Integer stYear, String vtCode) throws ITFEBizException;

}