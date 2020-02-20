package com.cfcc.itfe.service.util;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsCheckdeloptlogDto;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

public class WriteOperLog {
	private static Log logger = LogFactory.getLog(WriteOperLog.class);

	/**
	 * ��¼��־
	 * 
	 * @param userID
	 * @param sFuncCode
	 * @param menuName
	 * @param serviceName
	 * @param operFlag
	 * @param loginInfo
	 * @throws ITFEBizException 
	 * @throws TASBizException
	 */
	public static void operLog(String userID, String sFuncCode,
			String menuName, String serviceName, boolean operFlag,
			ITFELoginInfo loginInfo) throws ITFEBizException {

		TsSyslogDto log = new TsSyslogDto();
		// ��ˮ��
		try {
			log.setIno(new Integer(MsgSeqFacade.getlogSeq()));
		} catch (NumberFormatException e2) {
			logger.error("ȡSEQ�쳣", e2);
			logger.error("��־����:" + log.toString());
			throw new ITFEBizException("��־����:" + log.toString(),e2);
		} catch (SequenceException e2) {
			logger.error("ȡSEQ�쳣", e2);
			logger.error("��־����:" + log.toString());
			throw new ITFEBizException("��־����:" + log.toString(),e2);
		}
		// �û�id
		log.setSusercode(userID);
		// ����
		log.setSdate(TimeFacade.getCurrentStringTime());
		// ʱ��
		try {
			log.setStime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			logger.error("ȡʱ��������쳣!", e1);
			throw new ITFEBizException("��־����:" + log.toString(),e1);
		}
		// ҵ��������
		log.setSoperationtypecode(sFuncCode);
		// �����������
		log.setSorgcode(loginInfo.getSorgcode());
		// ����˵��
		log.setSoperationdesc(menuName);
		// ��ע
		if (operFlag) {
			log.setSdemo("����δ�ɹ�");
		}
		if (serviceName.equals("batchConfirm")) {
			log.setSdemo("��������:ȷ���ύ");
		} else if (serviceName.equals("batchDelete")) {
			log.setSdemo("��������:ɾ��");
		} else if (serviceName.equals("eachConfirm")) {
			log.setSdemo("������ţ�ȷ���ύ");
		} else if (serviceName.equals("eachDelete")) {
			log.setSdemo("������ţ�ɾ��");
		} else if (serviceName.equals("directSubmit")) {
			log.setSdemo("ֱ���ύ");
		}
		// д���ݿ����
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			logger.error("��¼���ݿ���־�����쳣", e);
			throw new ITFEBizException("��־����:" + log.toString(),e);
		}
	}

	/**
	 * д����ɾ��������־
	 * 
	 * @param s_biztype
	 * @param s_filename
	 * @param s_vouno
	 * @param loginInfo
	 * @throws ITFEBizException 
	 */
	public static void checkDelOptLog(String s_biztype, String s_filename,
			String s_vouno,BigDecimal famt, ITFELoginInfo loginInfo) throws ITFEBizException {

		TsCheckdeloptlogDto log = new TsCheckdeloptlogDto();
		// ��������
		log.setSorgcode(loginInfo.getSorgcode());
		//ҵ������
		log.setSbiztype(s_biztype);
		// �û�����
		log.setSusercode(loginInfo.getSuserCode());
		//�û�����
		log.setSusername(loginInfo.getSuserName());
		//�ļ���
		log.setSfilename(s_filename);
		//ƾ֤���
		log.setSvouno(s_vouno);
		// ����ʱ��
		try {
			log.setTopetime(TSystemFacade.getDBSystemTime());
		} catch (JAFDatabaseException e1) {
			logger.error("��¼���ݿ���־�����쳣", e1);
			throw new ITFEBizException("��־����:" + log.toString(),e1);
		}
		//���
		log.setFamt(famt);
		// д���ݿ����
		try {
			DatabaseFacade.getDb().create(log);
		} catch (JAFDatabaseException e) {
			logger.error("��¼���ݿ���־�����쳣", e);
			throw new ITFEBizException("��־����:" + log.toString(),e);
		}
	}
}
