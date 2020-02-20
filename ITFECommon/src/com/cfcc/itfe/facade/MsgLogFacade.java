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
 * 记日志处理
 * 
 * @author
 * 
 */
public class MsgLogFacade {

	private static Log logger = LogFactory.getLog(MsgLogFacade.class);

	/**
	 * * 记录接收日志
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
			dto.setSrecvno(_srecvno); // 接收流水号
			dto.setSsendno(_ssendno); // 对应发送日志流水号
			dto.setSrecvorgcode(_srecvorgcode);// 接收机构代码
			dto.setSdate(_sdate); // 所属日期 填批量头中的委托日期
			dto.setSoperationtypecode(_soperationtypecode);// 报文编号
			dto.setSsendorgcode(_ssendorgcode);// 接收机构
			dto.setStitle(_stitle);// 填写报文存放路径
			dto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));// 报文接收时间
			dto.setIcount(_icount); // 笔数
			dto.setNmoney(_nmoney);// 金额
			dto.setSpackno(_spackno);// 包流水号
			dto.setStrecode(_strecode);// 国库代码
			dto.setSpayeebankno(_spayeebankno);// 收款行行号
			dto.setSbillorg(_sbillorg); // 出票单位
			dto.setSpayoutvoutype(_spayoutvoutype); // 支出凭证类型
			dto.setSseq(_sseq);// MSGID报文ID
			dto.setSretcode(_sretcode);// 处理码
			dto.setSsenddate(TimeFacade.getCurrentStringTime());// 接收日期
			dto.setSretcodedesc(_sretcodedesc);// 处理码说明
			dto.setSproctime(_sproctime);// 处理时间
			dto.setSifsend(_sifsend);// 是否转发，发起方。前置发起为0，Tips发起为1，财政发起为2
			dto.setSturnsendflag(_sturnsendflag);// 转发标志
			if (null != _sdemo) {
				_sdemo = _sdemo.getBytes().length >= 100 ? _sdemo.substring(0, 50) : _sdemo;
			}
			dto.setSdemo(_sdemo);
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("记接收日志出错", e);
		}
	}



	/**
	 * * 记录发送日志
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
			dto.setSsendno(_ssendno); // 发送流水号
			dto.setSrecvno(_srecvno); // 对应接收流水号
			dto.setSsendorgcode(_ssendorgcode);// 发起机构代码
			dto.setSrecvorgcode(_srecvorgcode); // 接收机构代码
			dto.setSdate(_sdate);// 所属日期
			dto.setSoperationtypecode(_msgno);// 报文编号
			dto.setSsendtime(new Timestamp(new java.util.Date().getTime()));// 发送时间
			dto.setStitle(_spath); // 报文保存路径
			dto.setIcount(count); // 笔数
			dto.setNmoney(nmoney); // 金额
			dto.setSpackno(_spackno);// 包流水号
			dto.setStrecode(_strecode);// 国库代码
			dto.setSpayeebankno(_spayeebankno);// 收款行行号
			dto.setSbillorg(_sbillorg); // 出票单位
			dto.setSpayoutvoutype(_spayoutvoutype); // 支出凭证类型
			dto.setSseq(_sseq);// MSGID报文ID
			dto.setSretcode(_sretcode);// 处理码
			dto.setSsenddate(TimeFacade.getCurrentStringTime());// 发送日期
			dto.setSretcodedesc(_sretcodedesc);// 处理码说明
			dto.setSproctime(_sproctime);// 处理时间
			dto.setSifsend(_sifsend);// 是否转发，发起方。前置发起为0，Tips发起为1，财政发起为2
			dto.setSturnsendflag(_sturnsendflag);// 转发标志
			if (null != _sdemo) {
				_sdemo = _sdemo.getBytes().length >= 300 ? _sdemo.substring(0, 150) : _sdemo;
			}
			dto.setSdemo(_sdemo);
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("记发送日志出错", e);
		}
	}

	
	/**
	 * * 记录发送日志
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
			dto.setSsendno(_ssendno); // 发送流水号
			dto.setSrecvno(_srecvno); // 对应接收流水号
			dto.setSsendorgcode(_ssendorgcode);// 发起机构代码
			dto.setSrecvorgcode(_srecvorgcode); // 接收机构代码
			dto.setSdate(_sdate);// 所属日期
			dto.setSoperationtypecode(_msgno);// 报文编号
			dto.setSsendtime(new Timestamp(new java.util.Date().getTime()));// 发送时间
			dto.setStitle(_spath); // 报文保存路径
			dto.setIcount(count); // 笔数
			dto.setNmoney(nmoney); // 金额
			dto.setSpackno(_spackno);// 包流水号
			dto.setStrecode(_strecode);// 国库代码
			dto.setSpayeebankno(_spayeebankno);// 收款行行号
			dto.setSbillorg(_sbillorg); // 出票单位
			dto.setSpayoutvoutype(_spayoutvoutype); // 支出凭证类型
			dto.setSseq(_sseq);// MSGID报文ID
			dto.setSretcode(_sretcode);// 处理码
			dto.setSsenddate(TimeFacade.getCurrentStringTime());// 发送日期
			dto.setSretcodedesc(_sretcodedesc);// 处理码说明
			dto.setSproctime(_sproctime);// 处理时间
			dto.setSifsend(_sifsend);// 是否转发，发起方。前置发起为0，Tips发起为1，财政发起为2
			dto.setSturnsendflag(_sturnsendflag);// 转发标志
			if (null != _sdemo) {
				_sdemo = _sdemo.getBytes().length >= 300 ? _sdemo.substring(0, 150) : _sdemo;
			}
			dto.setSdemo(_sdemo);//备注
			return (TvSendlogDto) DatabaseFacade.getDb().createWithResult(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("记发送日志出错", e);
		}
	}

	/**
	 * 
	 * 记录错误信息日志
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
				
				//重复打出错误信息，避免有的错误出来后没有打log日志的情况
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
			logger.error("记录错误日志出现异常", e);
			throw new ITFEBizException("记录错误日志出现异常", e);
		}
	}
	/**
	  日志中需要发送方为tips,转变标志为1 的需要转发给财政
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
	 * 记录接收财政并转发TIPS的MQ报文信息
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
			logger.error("保存【记录接收财政并转发TIPS的MQ报文信息】出现异常", e);
		}
	}
	
	/**
	 * 查询原发起报文的MQMSGID
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
			logger.error("保存【记录接收财政并转发TIPS的MQ报文信息】出现异常", e);
			return null;
		} catch (ValidateException e) {
			logger.error("保存【记录接收财政并转发TIPS的MQ报文信息】出现异常", e);
			return null;
		}
	}
}
