package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


/**
 *（报文编号涉及：5102直接支付额度-财政发起直接支付额度转发tips）
 * 
 * @author zhouchuan
 * 
 */
public class Recv5102MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv5102MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// 解析报文头 headMap
			//(String) headMap.get("DES");// 接收机构代码
		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String orgcode = sendorgcode;


		/**
		 * 解析批量业务头 msgMap-->BatchHead5102
		 */
		HashMap batchhead5102 = (HashMap) msgMap.get("BatchHead5102");
		// 出票单位
		String finorgcode = (String) batchhead5102.get("BillOrg");
		// 国库代码
		String trecode = (String) batchhead5102.get("TreCode");
		// 委托日期
		String entrustdate = (String) batchhead5102.get("EntrustDate");
		// 包流水号
		String packno = (String) batchhead5102.get("PackNo");
		// 总笔数
		Integer allnum = Integer.valueOf((String) batchhead5102.get("AllNum"));
		// 总金额
		BigDecimal allamt = MtoCodeTrans.transformBigDecimal(batchhead5102.get("AllAmt"));
		
		/**
		 * 解析批量业务信息 msgMap-->Bill5102
		 */
		List<Object> bill5102 = (List<Object>) msgMap.get("Bill5102");
		String agentBankCode="";//经办单位(代理行号)
		if(bill5102!=null&&bill5102.size()>0){
			HashMap bill5102Map=(HashMap) bill5102.get(0);
			agentBankCode=(String) bill5102Map.get("RransactOrg");
		}else{
			return;
		}
		
		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		TsConvertfinorgDto finorgdto = new TsConvertfinorgDto();//根据国库代码取核算主体代码
		finorgdto.setStrecode(trecode);
		
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			List<TsConvertfinorgDto> orglist = CommonFacade.getODB().findRsByDto(finorgdto);
			if(orglist!=null&&orglist.size()>0){
				orgcode = orglist.get(0).getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收日志SEQ出错");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据国库代码查询核算主体出错");
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("根据国库代码查询核算主体出错");
		}
		//已经上线无纸化的代理行不转发
		String info=BusinessFacade.isRelayMsgToTips(orgcode, trecode, msgNo, agentBankCode);
		if(!info.equals("")){
			// 记接收日志
			MsgLogFacade.writeRcvLog(recvseqno, sendseqno, orgcode, entrustdate, (String) headMap.get("MsgNo"),
					(String) headMap.get("SRC"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, null, null,
					finorgcode, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, info);
			eventContext.setStopFurtherProcessing(true);
			return;
		}
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, orgcode, entrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, null, null,
				finorgcode, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, orgcode, (String) headMap.get("DES"), entrustdate,
				(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, null,
				null, finorgcode, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
		// 记录接收财政的消息记录
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, entrustdate, packno, jmsMessageID, jmsCorrelationID, finorgcode);
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}	

