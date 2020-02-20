/**
 * 实时扣税冲正回执
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.deptone.common.core.exception.MessageException;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangtuo
 * 
 */
public class Recv2021MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1021MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap rushreturn2021 = (HashMap) msgMap.get("RushReturn2021");

		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
//		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
//		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String msgref = (String) headMap.get("MsgRef"); // 报文id号
		String sbookorgcode=orgcode;

		// 解析冲正应答信息 msgMap -->RushReturn2021
		String taxorgcode = (String) rushreturn2021.get("TaxOrgCode");// 征收机关代码
		String oricanceldate = (String) rushreturn2021.get("OriCancelDate");// 原冲正申请委托日期
		String oricancelno = (String) rushreturn2021.get("OriCancelNo");// 原冲正申请序号
		String oritrano = (String) rushreturn2021.get("OriTraNo");// 原交易流水号
		String orientrustdate = (String) rushreturn2021.get("OriEntrustDate");// 原委托日期
		String cancelanswer = (String) rushreturn2021.get("CancelAnswer");// 冲正应答
		String addword = (String) rushreturn2021.get("AddWord");// 附言

		// 组织DTo准备更新数据******************************************************

		// 冲正信息查询dto TvTaxCancelDto cancelquerydto
		TvTaxCancelDto cancelquerydto = new TvTaxCancelDto();
		cancelquerydto.setStaxorgcode(taxorgcode);// 征收机关代码
		cancelquerydto.setScancelappno(oricancelno);// 冲正申请号
		cancelquerydto.setSorientrustdate(orientrustdate);// 原委托日期

		// 更新数据 ,可能有dto是空的情况
		if (null != cancelquerydto) {
			try {
				// 查询冲正申请对应信息
				List<TvTaxCancelDto> cancellist = CommonFacade.getODB()
						.findRsByDto(cancelquerydto);

				if (null != cancellist && 0 != cancellist.size()) {
					// 回执状态更新
					// 更新对应冲正信息 --冲正应答状态
					// 冲正信息更新dto TvTaxCancelDto updatedto
					TvTaxCancelDto updatedto = new TvTaxCancelDto();
					updatedto.setSseq(cancellist.get(0).getSseq());// 征收机关代码
					updatedto.setStaxorgcode(taxorgcode);// 征收机关代码
					updatedto.setScancelappno(oricancelno);// 冲正申请号
					updatedto.setSorientrustdate(orientrustdate);// 原委托日期
					updatedto.setSentrustdate(oricanceldate);// 委托日期
					updatedto.setSmsgid(msgid);// 报文标识号
					updatedto.setSoritrano(oritrano);// 原交易流水号
					updatedto.setCcancelanswer(cancelanswer);// 冲正应答
					updatedto.setScancelreason(cancellist.get(0)
							.getScancelreason());// 冲正原因
					updatedto.setTsupdate(new Timestamp(new java.util.Date()
							.getTime()));// 更新时间
					updatedto.setSaddword(addword);// 附言
					/*
					 * 更新对应冲正信息 --冲正应答状态、更新时间、附言
					 */
					DatabaseFacade.getDb().update(updatedto);
				}

			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("更新数据库出错", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("更新数据库出错", e);
			}
		}
		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水、
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(taxorgcode);
			if (null!=_dto) {
				sbookorgcode =_dto.getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收/发送日志SEQ出错");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据财政代码取核算主体代码失败！");
		}
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, orientrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), oricancelno, null, null, taxorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV_TIPS);

		// 判断是否需要转发给财政，如果为2则转发给财政并记发送日志
		TvSendlogDto tvsendlog = null;
		List tvsendloglist = null;
		try {
			tvsendlog = new TvSendlogDto();
			tvsendlog.setSbillorg(taxorgcode);
			tvsendlog.setSdate(orientrustdate);
			tvsendlog.setSpackno(oricancelno);
			tvsendloglist = CommonFacade.getODB().findRsByDto(tvsendlog);
		    if (null!=tvsendloglist && tvsendloglist.size()> 0 ) {
		    	tvsendlog = (TvSendlogDto) tvsendloglist.get(0);
				tvsendlog.setSretcode(DealCodeConstants.DEALCODE_TIPS_SUCCESS);
				tvsendlog.setSdemo(addword);
				tvsendlog
						.setSproctime(new Timestamp(new java.util.Date().getTime()));
				DatabaseFacade.getDb().update(tvsendlog);
			} 
		} catch (JAFDatabaseException e1) {
			logger.error("更新发送日志处理状态出现数据库异常!", e1);
			throw new ITFEBizException("更新发送日志处理状态出现数据库异常!", e1);
		} catch (ValidateException e) {
			logger.error("更新发送日志处理状态出现数据库异常!", e);
			throw new ITFEBizException("更新发送日志处理状态出现数据库异常!", e);
		}
		if (StateConstant.MSG_SENDER_FLAG_2.equals(tvsendlog.getSifsend())) {
			// 写发送日志
			MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, taxorgcode, orientrustdate,
					(String) headMap.get("MsgNo"), (String) eventContext
							.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
					new BigDecimal(0), oricancelno, null, null, taxorgcode, null,
					(String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
			Object msg = eventContext.getMessage().getProperty("MSG_INFO");// 获得原报文，重新发送
			if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
			{
				//取得原发起报文的MQMSGID
				TvMqmessageDto dto = MsgLogFacade.queryMQMSGID("1021", msgref);
				String correlationId = "ID:524551000000000000000000000000000000000000000000";
				if(dto==null){
					eventContext.getMessage().setCorrelationId(correlationId);
				}else{
					correlationId = dto.getSmqmsgid();
					eventContext.getMessage().setCorrelationId(correlationId);
				}
				try {
					String orimsgno = "1021";
					if(MsgConstant.BATCH_MSG_NO.contains(orimsgno)){//走批量队列的回执发送到批量队列
						if(orgcode!=null&&orgcode.startsWith("000002700009"))
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCHCITY, (String)msg, correlationId, false,orgcode);
						else
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_BATCH, (String)msg, correlationId, false,orgcode);
					}else{
						if(orgcode!=null&&orgcode.contains("000002700009"))
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,orgcode);
						else
							JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,orgcode);
					}
					eventContext.setStopFurtherProcessing(true);// 不做进一步处理
				} catch (MessageException e) {
					logger.error(e);
					throw new ITFEBizException("不用yak发送，自发送报文设置时效性失败：",e);
				}
			}else
				eventContext.getMessage().setPayload(msg);
		} else {
			// 不做进一步处理
			eventContext.setStopFurtherProcessing(true);
			return;
		}
	}
}
