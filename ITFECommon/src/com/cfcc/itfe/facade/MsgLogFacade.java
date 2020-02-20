package com.cfcc.itfe.facade;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvExceptionmanDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ����־����
 * 
 * @author
 * 
 */
public class MsgLogFacade {

	private static Log logger = LogFactory.getLog(MsgLogFacade.class);

	/**
	 * * ��¼������־
	 * 
	 * @param _srecvno
	 * @param _ssendno
	 * @param _srecvorgcode
	 * @param _sdate
	 * @param _soperationtypecode
	 * @param _ssendorgcode
	 * @param _stitle
	 * @param _icount
	 * @param _nmoney
	 * @param _spackno
	 * @param _strecode
	 * @param _spayeebankno
	 * @param _sbillorg
	 * @param _spayoutvoutype
	 * @param _sseq
	 * @throws ITFEBizException
	 */
	public static void writeRcvLog(String _srecvno, String _ssendno,
			String _srecvorgcode, String _sdate, String _soperationtypecode,
			String _ssendorgcode, String _stitle, int _icount,
			BigDecimal _nmoney, String _spackno, String _strecode,
			String _spayeebankno, String _sbillorg, String _spayoutvoutype,
			String _sseq, String _sretcode, String _sretcodedesc, Timestamp _sproctime,
			String _sifsend, String _sturnsendflag,String _sdemo) throws ITFEBizException {
		try {
			TvRecvlogDto dto = new TvRecvlogDto();
			dto.setSrecvno(_srecvno); // ������ˮ��
			dto.setSsendno(_ssendno); // ��Ӧ������־��ˮ��
			dto.setSrecvorgcode(_srecvorgcode);// ���ջ�������
			dto.setSdate(_sdate); // �������� ������ͷ�е�ί������
			dto.setSoperationtypecode(_soperationtypecode);// ���ı��
			dto.setSsendorgcode(_ssendorgcode);// ���ջ���
			dto.setStitle(_stitle);// ��д���Ĵ��·��
			dto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));// ���Ľ���ʱ��
			dto.setIcount(_icount); // ����
			dto.setNmoney(_nmoney);// ���
			dto.setSpackno(_spackno);// ����ˮ��
			dto.setStrecode(_strecode);// �������
			dto.setSpayeebankno(_spayeebankno);// �տ����к�
			dto.setSbillorg(_sbillorg); // ��Ʊ��λ
			dto.setSpayoutvoutype(_spayoutvoutype); // ֧��ƾ֤����
			dto.setSseq(_sseq);// MSGID����ID
			dto.setSretcode(_sretcode);// ������
			dto.setSsenddate(TimeFacade.getCurrentStringTime());// ��������
			dto.setSretcodedesc(_sretcodedesc);// ������˵��
			dto.setSproctime(_sproctime);// ����ʱ��
			dto.setSifsend(_sifsend);// �Ƿ�ת�������𷽡�ǰ�÷���Ϊ0��Tips����Ϊ1����������Ϊ2
			dto.setSturnsendflag(_sturnsendflag);// ת����־
			if (null != _sdemo) {
				_sdemo = _sdemo.getBytes().length >= 100 ? _sdemo.substring(0, 50) : _sdemo;
			}
			dto.setSdemo(_sdemo);
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("�ǽ�����־����", e);
		}
	}



	/**
	 * * ��¼������־
	 * 
	 * @param _ssendno
	 * @param _srecvno
	 * @param _ssendorgcode
	 * @param _srecvorgcode
	 * @param _sdate
	 * @param _msgno
	 * @param _spath
	 * @param count
	 * @param nmoney
	 * @param _spackno
	 * @param _strecode
	 * @param _spayeebankno
	 * @param _sbillorg
	 * @param _spayoutvoutype
	 * @param _sseq
	 * @throws ITFEBizException
	 */
	public static void writeSendLog(String _ssendno, String _srecvno,
			String _ssendorgcode, String _srecvorgcode, String _sdate,
			String _msgno, String _spath, int count, BigDecimal nmoney,
			String _spackno, String _strecode, String _spayeebankno,
			String _sbillorg, String _spayoutvoutype, String _sseq,
			String _sretcode, String _sretcodedesc, Timestamp _sproctime,
			String _sifsend, String _sturnsendflag,String _sdemo) throws ITFEBizException {
		try {
			TvSendlogDto dto = new TvSendlogDto();
			dto.setSsendno(_ssendno); // ������ˮ��
			dto.setSrecvno(_srecvno); // ��Ӧ������ˮ��
			dto.setSsendorgcode(_ssendorgcode);// �����������
			dto.setSrecvorgcode(_srecvorgcode); // ���ջ�������
			dto.setSdate(_sdate);// ��������
			dto.setSoperationtypecode(_msgno);// ���ı��
			dto.setSsendtime(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
			dto.setStitle(_spath); // ���ı���·��
			dto.setIcount(count); // ����
			dto.setNmoney(nmoney); // ���
			dto.setSpackno(_spackno);// ����ˮ��
			dto.setStrecode(_strecode);// �������
			dto.setSpayeebankno(_spayeebankno);// �տ����к�
			dto.setSbillorg(_sbillorg); // ��Ʊ��λ
			dto.setSpayoutvoutype(_spayoutvoutype); // ֧��ƾ֤����
			dto.setSseq(_sseq);// MSGID����ID
			dto.setSretcode(_sretcode);// ������
			dto.setSsenddate(TimeFacade.getCurrentStringTime());// ��������
			dto.setSretcodedesc(_sretcodedesc);// ������˵��
			dto.setSproctime(_sproctime);// ����ʱ��
			dto.setSifsend(_sifsend);// �Ƿ�ת�������𷽡�ǰ�÷���Ϊ0��Tips����Ϊ1����������Ϊ2
			dto.setSturnsendflag(_sturnsendflag);// ת����־
			if (null != _sdemo) {
				_sdemo = _sdemo.getBytes().length >= 300 ? _sdemo.substring(0, 150) : _sdemo;
			}
			dto.setSdemo(_sdemo);
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("�Ƿ�����־����", e);
		}
	}

	
	/**
	 * * ��¼������־
	 * 
	 * @param _ssendno
	 * @param _srecvno
	 * @param _ssendorgcode
	 * @param _srecvorgcode
	 * @param _sdate
	 * @param _msgno
	 * @param _spath
	 * @param count
	 * @param nmoney
	 * @param _spackno
	 * @param _strecode
	 * @param _spayeebankno
	 * @param _sbillorg
	 * @param _spayoutvoutype
	 * @param _sseq
	 * @throws ITFEBizException
	 */
	public static TvSendlogDto writeSendLogWithResult(String _ssendno,
			String _srecvno, String _ssendorgcode, String _srecvorgcode,
			String _sdate, String _msgno, String _spath, int count,
			BigDecimal nmoney, String _spackno, String _strecode,
			String _spayeebankno, String _sbillorg, String _spayoutvoutype,
			String _sseq, String _sretcode, String _sretcodedesc,
			Timestamp _sproctime, String _sifsend, String _sturnsendflag,String _sdemo)
			throws ITFEBizException {
		try {
			TvSendlogDto dto = new TvSendlogDto();
			dto.setSsendno(_ssendno); // ������ˮ��
			dto.setSrecvno(_srecvno); // ��Ӧ������ˮ��
			dto.setSsendorgcode(_ssendorgcode);// �����������
			dto.setSrecvorgcode(_srecvorgcode); // ���ջ�������
			dto.setSdate(_sdate);// ��������
			dto.setSoperationtypecode(_msgno);// ���ı��
			dto.setSsendtime(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
			dto.setStitle(_spath); // ���ı���·��
			dto.setIcount(count); // ����
			dto.setNmoney(nmoney); // ���
			dto.setSpackno(_spackno);// ����ˮ��
			dto.setStrecode(_strecode);// �������
			dto.setSpayeebankno(_spayeebankno);// �տ����к�
			dto.setSbillorg(_sbillorg); // ��Ʊ��λ
			dto.setSpayoutvoutype(_spayoutvoutype); // ֧��ƾ֤����
			dto.setSseq(_sseq);// MSGID����ID
			dto.setSretcode(_sretcode);// ������
			dto.setSsenddate(TimeFacade.getCurrentStringTime());// ��������
			dto.setSretcodedesc(_sretcodedesc);// ������˵��
			dto.setSproctime(_sproctime);// ����ʱ��
			dto.setSifsend(_sifsend);// �Ƿ�ת�������𷽡�ǰ�÷���Ϊ0��Tips����Ϊ1����������Ϊ2
			dto.setSturnsendflag(_sturnsendflag);// ת����־
			if (null != _sdemo) {
				_sdemo = _sdemo.getBytes().length >= 300 ? _sdemo.substring(0, 150) : _sdemo;
			}
			dto.setSdemo(_sdemo);//��ע
			return (TvSendlogDto) DatabaseFacade.getDb().createWithResult(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("�Ƿ�����־����", e);
		}
	}

	/**
	 * 
	 * ��¼������Ϣ��־
	 * 
	 * @param muleMessage
	 * @throws ITFEBizException
	 */
	public static void saveErrInfo(MuleMessage muleMessage)
			throws ITFEBizException {
		TvExceptionmanDto dto = new TvExceptionmanDto();
		
		if (muleMessage.getExceptionPayload() != null) {
			String s = muleMessage.getExceptionPayload().getRootException().getMessage();
			if (null != s) {
				
				//�ظ����������Ϣ�������еĴ��������û�д�log��־�����
				logger.error(s,muleMessage.getExceptionPayload().getException());
				
				dto.setSexceptioninfo(s.getBytes().length >= 1024 ? s
						.substring(0, 500) : s);
			}
			String msgno = (String) muleMessage.getProperty(MessagePropertyKeys.MSG_NO_KEY);
			if (null==msgno ) {
				msgno =MsgConstant.MSG_NO_0000;
			}
			dto.setSofbizkind(msgno);
			dto.setDworkdate(TimeFacade.getCurrentStringTime());
			dto.setTsupdate(new Timestamp(new java.util.Date().getTime()));
		} else {
			return;
		}
		try {
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error("��¼������־�����쳣", e);
			throw new ITFEBizException("��¼������־�����쳣", e);
		}
	}
	/**
	  ��־����Ҫ���ͷ�Ϊtips,ת���־Ϊ1 ����Ҫת��������
	 * @param msgsender
	 * @param msgno
	 * @return
	 */
	
	public static boolean resendtofin(String msgsender,String turnSend){
		if(StateConstant.MSG_SENDER_FLAG_1.equals(msgsender)&& MsgConstant.OTHER_SEND.equals(turnSend)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * ��¼���ղ�����ת��TIPS��MQ������Ϣ
	 * @throws ITFEBizException 
	 */
	public static void writeMQMessageLog(String sendnode, String recvnode, String msgno, String msgid, String entrusdate,
			String packno, String mqmsgid, String mqcorrelid, String taxorgcode) {
		TvMqmessageDto dto = new TvMqmessageDto();
		dto.setSsendnode(sendnode);
		dto.setSrecvnode(recvnode);
		dto.setSmsgno(msgno);
		dto.setSmsgid(msgid);
		dto.setSentrustdate(entrusdate);
		dto.setSpackno(packno);
		dto.setSmqmsgid(mqmsgid);
		if (null==mqcorrelid || mqcorrelid.trim().length()==0) {
			dto.setScorrelid("");
		} else {
			dto.setScorrelid(mqcorrelid);
		}
		
		dto.setStaxorgcode(taxorgcode);
		dto.setSsysdate(new Timestamp(TimeFacade.getCurrentDateTime().getTime()));
		try {
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error("���桾��¼���ղ�����ת��TIPS��MQ������Ϣ�������쳣", e);
		}
	}
	
	/**
	 * ��ѯԭ�����ĵ�MQMSGID
	 */
	public static TvMqmessageDto queryMQMSGID(String msgno, String refmsgid){
		TvMqmessageDto dto = new TvMqmessageDto();
		dto.setSmsgno(msgno);
		dto.setSmsgid(refmsgid);
		try {
			List list = CommonFacade.getODB().findRsByDto(dto);
			if(list.size()==0 || list == null){
				return null;
			}else{
				return (TvMqmessageDto) list.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error("���桾��¼���ղ�����ת��TIPS��MQ������Ϣ�������쳣", e);
			return null;
		} catch (ValidateException e) {
			logger.error("���桾��¼���ղ�����ת��TIPS��MQ������Ϣ�������쳣", e);
			return null;
		}
	}
}
