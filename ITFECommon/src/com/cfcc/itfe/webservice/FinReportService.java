package com.cfcc.itfe.webservice;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.webservice.jaxws.NoticeDelegate;
import com.cfcc.itfe.webservice.jaxws.NoticeServiceLocator;

public class FinReportService {
	private static Log logger = LogFactory.getLog(FinReportService.class);
	
	public FinReportService (){
		
	}
	
	//调用财政端通知接口
	public void readReportNotice(String finorgcode, String reportdate,String biztype,String reportcount,String [] msgid) throws UnsupportedEncodingException { 
		try {
			logger.debug("===============调用财政端通知接口,告知财政开始取数:"+finorgcode+"_"+reportdate+"_"+biztype+"_"+reportcount+"===============");
			NoticeDelegate service =new NoticeServiceLocator().getNoticePort();
			service.readReportNotice(finorgcode, reportdate, biztype, reportcount,msgid);
			logger.debug("======================调用财政端通知接口结束!"+finorgcode+"_"+reportdate+"_"+biztype+"_"+reportcount+"===============");
		} catch (RemoteException e) {
			logger.error("调用财政端通知接口出错："+e);
			throw new UnsupportedEncodingException("调用财政段通知接口出错：\n"+e);
		} catch (ServiceException e) {
			logger.error("调用财政端通知接口出错："+e);
			throw new UnsupportedEncodingException("调用财政段通知接口出错：\n"+e);
		}
	} 
}
