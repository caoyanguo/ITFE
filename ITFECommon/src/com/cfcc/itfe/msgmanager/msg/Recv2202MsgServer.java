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
 *（报文编号涉及：2202商业银行办理支付无（有）纸凭证退款请求转发tips）
 * 
 * @author zhouchuan
 * 
 */
public class Recv2202MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2202MsgServer.class);

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
		 * 解析批量业务头 msgMap-->BatchHead2202
		 */
		HashMap batchhead2202 = (HashMap) msgMap.get("Head2202");
		// 出票单位
		String finorgcode = (String) batchhead2202.get("AgentBnkCode");
		// 国库代码
		String trecode = (String) batchhead2202.get("TreCode");
		// 委托日期
		String entrustdate = (String) batchhead2202.get("EntrustDate");
		// 包流水号
		String packno = (String) batchhead2202.get("PackNo");
		// 总笔数
		Integer allnum = Integer.valueOf((String) batchhead2202.get("AllNum"));
		// 总金额
		BigDecimal allamt = MtoCodeTrans.transformBigDecimal(batchhead2202.get("AllAmt"));
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
		String info=BusinessFacade.isRelayMsgToTips(orgcode, trecode, msgNo, finorgcode);
		if(!info.equals("")){
			// 记接收日志
			MsgLogFacade.writeRcvLog(recvseqno, sendseqno, orgcode, entrustdate, (String) headMap.get("MsgNo"),
					(String) headMap.get("SRC"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, trecode, null,
					finorgcode, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, info);
			eventContext.setStopFurtherProcessing(true);
			return;
		}
		
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, orgcode, entrustdate, msgNo,
				(String) headMap.get("SRC"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, trecode, null,
				finorgcode, null, msgid,DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, orgcode, (String) headMap.get("DES"), entrustdate,
				msgNo, (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, trecode,
				null, finorgcode, null, msgid,DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}	

