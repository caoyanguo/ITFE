package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

public class Recv9105MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9105MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			String msgRef = (String) headMap.get("MsgRef");
			String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
			HashMap FreeFormat9105 = (HashMap) msgMap.get("FreeFormat9105");
			String ls_SrcNodeCode = (String) FreeFormat9105.get("SrcNodeCode");
			String ls_DesNodeCode = (String) FreeFormat9105.get("DesNodeCode");
			String ls_SendOrgCode = (String) FreeFormat9105.get("SendOrgCode");
			String ls_RcvOrgCode = (String) FreeFormat9105.get("RcvOrgCode");
			String ls_Content = (String) FreeFormat9105.get("Content");

			String msgid = (String) headMap.get("MsgID");
			String sdate = (String) headMap.get("WorkDate");
			String sendno = null;// 发送流水号
			String sbillorg = null;// 出票单位
			String packno = null;// 原包号
			String strecode = null;// 国库代码
			String sdemo = "收到9105回执报文";
			// 转换处理码
			// String state =
			// PublicSearchFacade.getPackageStateByDealCode(ls_OpStat);
			// 接收日志流水d
			String recvseqno = StampFacade.getStampSendSeq("JS");
			String sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			// 查找原发送日志

			String recvorg = (String) headMap.get("DES");
			String sendorg = (String) headMap.get("SRC");
			String biztype = (String) headMap.get("MsgNo");
			String filepath = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			String stamp = TimeFacade.getCurrentStringTime();
			String ifsend = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_SENDER);

			// // 记录接收日志
			// TvRecvlogDto dto = new TvRecvlogDto();
			// dto.setSrecvno(_srecvno); // 接收流水号
			// dto.setSsendno(sendno); // 对应发送日志流水号
			// dto.setSrecvorgcode(ls_SendOrgCode);// 接收机构代码
			// dto.setSdate(sdate); // 所属日期 填批量头中的委托日期
			// dto.setSoperationtypecode(MsgConstant.MSG_NO_9105);// 报文编号
			// dto.setSsendorgcode(ls_RcvOrgCode);// 接收机构
			// dto.setStitle((String) eventContext.getMessage().getProperty(
			// "XML_MSG_FILE_PATH"));// 填写报文存放路径
			// dto.setSrecvtime(new Timestamp(new
			// java.util.Date().getTime()));// 报文接收时间
			// dto.setIcount(0); // 笔数
			// dto.setNmoney(new BigDecimal(0));// 金额
			// dto.setSpackno("");// 包流水号
			// dto.setStrecode("");// 国库代码
			// dto.setSpayeebankno("");// 收款行行号
			// dto.setSbillorg(""); // 出票单位
			// dto.setSpayoutvoutype(""); // 支出凭证类型
			// dto.setSusercode("");
			// dto.setSseq((String) headMap.get("MsgID"));// MSGID报文ID
			// dto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIVER);// 处理码
			// dto.setSsenddate(TimeFacade.getCurrentStringTime());// 接收日期
			// dto.setSretcodedesc(sdemo);// 处理码说明
			// dto.setSproctime(new Timestamp(new
			// java.util.Date().getTime()));// 处理时间
			// dto.setSifsend("0");// 是否转发，发起方。前置发起为0，Tips发起为1，财政发起为2
			// dto.setSturnsendflag("");// 转发标志
			// if (null != sdemo) {
			// sdemo = sdemo.getBytes().length >= 300 ? sdemo
			// .substring(0, 150) : sdemo;
			// }
			// dto.setSdemo(ls_Content);
			// dto.setSbatch("");
			// dto.setSsenddate("");
			// DatabaseFacade.getDb().create(dto);

			if (!"100000000000".equals(sendorgcode)) {	//接收财政发送处理，接收后直接转发
				// 接了接收日志
				MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sendorgcode,
						sdate, (String) headMap.get("MsgNo"), (String) headMap
								.get("SRC"), (String) eventContext.getMessage()
								.getProperty("XML_MSG_FILE_PATH"), 1,
						BigDecimal.ZERO, packno, null, null, null, null,
						(String) headMap.get("MsgID"),
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null,
						MsgConstant.LOG_ADDWORD_RECV);
				
			
				// 记录接收财政的消息记录
				String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
				String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
				MsgLogFacade.writeMQMessageLog(sendorgcode, sendorgcode, (String) headMap.get("MsgNo"), msgid, sdate, 
						packno, jmsMessageID, jmsCorrelationID, null);
				
				// 写发送日志
				MsgLogFacade.writeSendLog(sendseqno, recvseqno, sendorgcode, (String) headMap.get("DES"), sdate,
						(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 1, BigDecimal.ZERO , packno, null,
						null, null, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
				
				// 获得原报文，重新发送
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");
				eventContext.getMessage().setPayload(msg);			
			}else{
				// 查找原发送日志
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
						MsgConstant.MSG_NO_9105);
				if (null != senddto) {
					// 更新原发送日志流水号
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIVER,
							recvseqno, sdemo);
					sendno = senddto.getSsendno();
					sbillorg = senddto.getSbillorg();
					// packno = senddto.getSpackno();
					strecode = senddto.getStrecode();
					
					
					Object msg = eventContext.getMessage().getProperty("MSG_INFO");
					if(strecode!=null&&strecode.startsWith("1702"))
						JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, "524551000000000000000000000000000000000000000000", false,strecode);
					else
						JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, "524551000000000000000000000000000000000000000000", false,strecode);
					eventContext.setStopFurtherProcessing(true);// 不做进一步处理
				}
			}
		} catch (Exception e) {
			logger.error("接收9105报文处理失败!", e);
			throw new ITFEBizException("接收9105报文处理失败", e);
		}
//		eventContext.setStopFurtherProcessing(true);
		return;
	}

}
