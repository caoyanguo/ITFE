package com.cfcc.itfe.client.common;

import java.awt.datatransfer.SystemFlavorMap;
import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cfcc.itfe.client.dialog.BudgetSubCodeDialog;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class QuartzDemo implements Job{  
	  
   
	public void execute() throws JobExecutionException {
		ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)ServiceFactory.getService(ICommonDataAccessService.class);
		// TODO Auto-generated method stub
		System.out.println("执行我.......不带参数"+TimeFacade.getCurrentStringTime("yyyy-MM-dd HH:mm:ss:ms"));  
		 
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		 System.out.println("执行我.......有参数"+TimeFacade.getCurrentStringTime("yyyy-MM-dd HH:mm:ss:ms"));  
		
	}  
  
}  