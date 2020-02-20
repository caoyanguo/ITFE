package com.cfcc.test;

import java.sql.Date;

import com.cfcc.jaf.core.invoker.handler.ServiceFactory;
import com.cfcc.jaf.core.loader.ContextFactory;

public class AcctProc_Test {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
//
//			ITBSIndexAcctProcessService iservice = (ITBSIndexAcctProcessService) ServiceFactory
//					.getService(ITBSIndexAcctProcessService.class);
//			TASRequestExtInfo req = new TASRequestExtInfo();
//
//			TASLoginInfo info = new TASLoginInfo();
//			info.setBookOrgCode("020000000000");
//			info.setBookrogacctdate(Date.valueOf("2009-01-07"));
//			info.setAcctDate(Date.valueOf("2009-01-07"));
//			req.setLoginInfo(info);
//			// iservice.ge
//		
//			iservice.allAcctProc();
			System.out.print("end");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
