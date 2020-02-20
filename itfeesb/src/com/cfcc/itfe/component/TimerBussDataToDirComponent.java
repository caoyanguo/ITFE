package com.cfcc.itfe.component;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.ExportBusinessDataCsv;

/**
 * 
 * �������󣺵���ʵ���ʽ𡢼���֧����ȡ�����֧�����ʱ����
 * ����ȫ������һ�Σ�����tcbs��ִ����һ��
 * 
 */
public class TimerBussDataToDirComponent implements Callable {
	//80000-�ɹ�,80001-ʧ��,	80002-������,80008-δ����,80009-������,80013-����ʧ��
	private static Log logger = LogFactory.getLog(TimerBussDataToDirComponent.class);
	public Object onCall(MuleEventContext eventContext) throws Exception {
		String datadate = TimeFacade.getCurrentStringTime();
		logger.info("ʵ���ʽ𡢼���֧����ȡ�����֧��������ļ�start=================================="+datadate);
		TvPayoutmsgmainDto payout = new TvPayoutmsgmainDto();//ʵ���ʽ�
		TvDirectpaymsgmainDto directpay = new TvDirectpaymsgmainDto();//ֱ��֧�����
		TvGrantpaymsgmainDto grantpay = new TvGrantpaymsgmainDto();//��Ȩ֧�����
		TvPayreckBankDto payreckbank = new TvPayreckBankDto();//���л���
		TvPayreckBankBackDto payreckbankbank = new TvPayreckBankBackDto();//�����˿�
		payout.setScommitdate(datadate);		
		directpay.setScommitdate(datadate);		
		grantpay.setScommitdate(datadate);		
		payreckbank.setDentrustdate(CommonUtil.strToDate(datadate));		
		payreckbankbank.setDentrustdate(CommonUtil.strToDate(datadate));
		ExportBusinessDataCsv exportDataUtil = new ExportBusinessDataCsv();
		exportDataUtil.exportPayoutData(payout, "",null);//����ʵ���ʽ���ȫ��״̬����
		exportDataUtil.exportPayoutBack(payout,null);//����ʵ���ʽ��˻ص���ȫ��״̬����
		exportDataUtil.exportDirectPayData(directpay, "",null);//����ֱ��֧����ȵ���ȫ��״̬����
		exportDataUtil.exportGrantPayData(grantpay, "",null);//������Ȩ֧����ȵ���ȫ��״̬����
		exportDataUtil.exportCommApplyPayData(payreckbank, "",null);//�������л����ȫ��״̬����
		exportDataUtil.exportCommApplyPayBackData(payreckbankbank, "",null);//���������˿��ȫ��״̬����
		exportDataUtil.exportPayoutData(payout, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//����ʵ���ʽ���TCBS��ִ������
		exportDataUtil.exportDirectPayData(directpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//����ֱ��֧����ȵ���TCBS��ִ������
		exportDataUtil.exportGrantPayData(grantpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//������Ȩ֧����ȵ���TCBS��ִ������
		exportDataUtil.exportCommApplyPayData(payreckbank, " AND (S_RESULT='80000' or S_RESULT='80001')",null);//�������л����TCBS��ִ������
		exportDataUtil.exportCommApplyPayBackData(payreckbankbank, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//���������˿��TCBS��ִ������
		exportDataUtil.recvLogCopyFile(datadate,"3143");//����������־��3143�ļ���ftpĿ¼
		exportDataUtil.recvLogCopyFile(datadate,"3144");//����������־��3144�ļ���ftpĿ¼
		logger.info("ʵ���ʽ𡢼���֧����ȡ�����֧��������ļ�end==================================");
		return eventContext;
	}
}
