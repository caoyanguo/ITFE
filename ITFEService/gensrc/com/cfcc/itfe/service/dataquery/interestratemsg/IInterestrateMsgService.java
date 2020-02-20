package com.cfcc.itfe.service.dataquery.interestratemsg;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TfInterestrateMsgDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:41
 * @generated
 * codecomment: 
 */
public interface IInterestrateMsgService extends IService {



	/**
	 * interestRate	 
	 * @generated
	 * @param mainDto
	 * @return java.lang.Void
	 * @throws ITFEBizException	 
	 */
   public abstract Void interestRate(TfInterestrateMsgDto mainDto) throws ITFEBizException;

}