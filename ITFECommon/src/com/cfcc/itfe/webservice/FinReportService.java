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
	
	//���ò�����֪ͨ�ӿ�
	public void readReportNotice(String finorgcode, String reportdate,String biztype,String reportcount,String [] msgid) throws UnsupportedEncodingException { 
		try {
			logger.debug("===============���ò�����֪ͨ�ӿ�,��֪������ʼȡ��:"+finorgcode+"_"+reportdate+"_"+biztype+"_"+reportcount+"===============");
			NoticeDelegate service =new NoticeServiceLocator().getNoticePort();
			service.readReportNotice(finorgcode, reportdate, biztype, reportcount,msgid);
			logger.debug("======================���ò�����֪ͨ�ӿڽ���!"+finorgcode+"_"+reportdate+"_"+biztype+"_"+reportcount+"===============");
		} catch (RemoteException e) {
			logger.error("���ò�����֪ͨ�ӿڳ���"+e);
			throw new UnsupportedEncodingException("���ò�����֪ͨ�ӿڳ���\n"+e);
		} catch (ServiceException e) {
			logger.error("���ò�����֪ͨ�ӿڳ���"+e);
			throw new UnsupportedEncodingException("���ò�����֪ͨ�ӿڳ���\n"+e);
		}
	} 
}
