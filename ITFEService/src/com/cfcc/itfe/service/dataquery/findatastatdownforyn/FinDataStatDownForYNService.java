package com.cfcc.itfe.service.dataquery.findatastatdownforyn;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TvUsersconditionDto;
import com.cfcc.itfe.service.dataquery.findatastatdownforyn.AbstractFinDataStatDownForYNService;
import com.cfcc.itfe.service.expreport.IMakeReport;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author db2admin
 * @time   13-04-18 14:21:17
 * codecomment: 
 */

public class FinDataStatDownForYNService extends AbstractFinDataStatDownForYNService {
	private static Log log = LogFactory.getLog(FinDataStatDownForYNService.class);

	public List makeRptFile(IDto idto) throws ITFEBizException {
		TrIncomedayrptDto incomedto = (TrIncomedayrptDto) idto;
		String bizType;
		String filepath;
		List <String> fileNameList = new ArrayList<String>();
		String sbookorgcode =getLoginInfo().getSorgcode();
		String root = ITFECommonConstant.FILE_ROOT_PATH ;
		
		IMakeReport makereport = (IMakeReport) ContextFactory
		.getApplicationContext().getBean(
				MsgConstant.SPRING_EXP_REPORT+"1_YN");
		filepath = makereport.makeReportByBiz(incomedto, "1",sbookorgcode);
		if (null!=filepath) {
			fileNameList.add(filepath.replaceAll(root, ""));
		} 
        
		return fileNameList;
	}	
	/**
	 * 查询科目代码条件信息
	 	 
	 * @generated
	 * @param tvUsersconditionDto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
	public String queryCondition(TvUsersconditionDto tvUsersconditionDto)
			throws ITFEBizException {
		String ls_conditions = "";
		try {
			tvUsersconditionDto = (TvUsersconditionDto) DatabaseFacade.getODB().find(tvUsersconditionDto);
			if(tvUsersconditionDto!=null){
				ls_conditions = tvUsersconditionDto.getSconditions();
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls_conditions;
	}
	/**
	 * 保持查询条件信息
	 	 
	 * @generated
	 * @param tvUsersconditionDto
	 * @throws ITFEBizException	 
	 */
	public void saveCondition(TvUsersconditionDto tvUsersconditionDto)
			throws ITFEBizException {
		try {
			String condition = queryCondition(tvUsersconditionDto);
			if("".equals(condition)){
				DatabaseFacade.getODB().create(tvUsersconditionDto);
			}else{
				DatabaseFacade.getODB().update(tvUsersconditionDto);
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}