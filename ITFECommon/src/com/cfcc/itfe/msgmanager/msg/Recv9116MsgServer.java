package com.cfcc.itfe.msgmanager.msg;

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

public class Recv9116MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9116MsgServer.class);

	/**
	 * 9116财政收入核对包重发请求 描述财政系统未收到财政收入核对包3122时，申请TIPS重新发送
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();

		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap getMsg9116Map = (HashMap) msgMap.get("GetMsg9116");

		// 解析报文中的请求信息CFX->MSG->GetMsg9116
		String endOrgCode = (String) getMsg9116Map.get("SendOrgCode"); // 发起机构代码
		String ntrustDate = (String) getMsg9116Map.get("EntrustDate"); // 委托日期
		String oriPackMsgNo = (String) getMsg9116Map.get("OriPackMsgNo");// 原包报文编号
		String oriChkDate = (String) getMsg9116Map.get("OriChkDate");// 原核对日期
		String oriChkAcctType = (String) getMsg9116Map.get("OriChkAcctType");// 原文件对账类型
		String oriReportMonth = (String) getMsg9116Map.get("OriReportMonth");// 原报表所属年月
		String oirDocNameMonth = (String) getMsg9116Map.get("OirDocNameMonth");// 原文件名所在年月

		String ls_SendSeq = "";
		String ls_RecvSeq = "";
		// 记接收日志
		try {
			ls_SendSeq = StampFacade.getStampSendSeq("FS");
			ls_RecvSeq =  StampFacade.getStampSendSeq("JS");
			TvRecvlogDto recvlogdto = new TvRecvlogDto();
			recvlogdto.setSrecvno(ls_RecvSeq);// 接收日志流水号
			recvlogdto.setSsendno(ls_SendSeq);// 对应发送日志流水号
			recvlogdto.setSrecvorgcode((String) headMap.get("DES")); // 接收机构代码
			recvlogdto.setSdate(TimeFacade.getCurrentStringTime());// 所属日期
			recvlogdto.setSoperationtypecode((String) headMap.get("MsgNo"));// 业务凭证类型
			recvlogdto.setSsendorgcode((String) headMap.get("SRC"));// 发送机构代码
			recvlogdto.setStitle((String) eventContext.getMessage()
					.getProperty("XML_MSG_FILE_PATH"));// 报文所对应文件的路径
			recvlogdto.setSrecvtime(new Timestamp(new java.util.Date()
					.getTime()));// 接收时间

			recvlogdto.setSretcode(""); // 处理码
			recvlogdto.setSretcodedesc(""); // 处理说明//
			recvlogdto.setSdemo("");// 详细信息
			recvlogdto.setSstate("");// 处理状态
			recvlogdto.setSbatch(""); // 批次号
			recvlogdto.setSbillorg(endOrgCode);
			recvlogdto.setSseq((String) headMap.get("MsgID"));
			recvlogdto.setSbatch(oriPackMsgNo);
			DatabaseFacade.getDb().create(recvlogdto);
		} catch (SequenceException e) {
			logger.error("取接收流水号失败!", e);
			throw new ITFEBizException("取接收流水号失败", e);
		} catch (JAFDatabaseException e) {
			logger.error("创建接收日志出现数据库异常!", e);
			throw new ITFEBizException("创建接收日志出现数据库异常!", e);
		}

		// 写发送日志
		try {

			TvSendlogDto sendlogdto = new TvSendlogDto();
			sendlogdto.setSsendno(ls_SendSeq);// 发送日志流水号
			sendlogdto.setSsendorgcode((String) headMap.get("SRC"));// 发送机构代码
			sendlogdto.setSdate(TimeFacade.getCurrentStringTime());// 所属日期
			sendlogdto.setSoperationtypecode((String) headMap.get("MsgNo"));// 业务凭证类型
			sendlogdto.setSrecvorgcode((String) headMap.get("DES")); // 接收机构代码，目前指财政系统
			sendlogdto.setStitle((String) eventContext.getMessage()
					.getProperty("XML_MSG_FILE_PATH"));// 报文所对应文件的路径
			sendlogdto.setSsendtime(new Timestamp(new java.util.Date()
					.getTime()));// 发送时间
			sendlogdto.setSretcode(""); // 处理码
			sendlogdto.setSdemo("");// 详细信息
			sendlogdto.setSstate("");// 处理状态
			sendlogdto.setSseq((String) headMap.get("MsgID")); // 报文id号
			sendlogdto.setSbillorg(endOrgCode);
			DatabaseFacade.getDb().create(sendlogdto);
		} catch (JAFDatabaseException e) {
			logger.error("创建发送日志出现数据库异常!", e);
			throw new ITFEBizException("创建发送日志出现数据库异常!", e);
		}

		// 修改payload，以便transformer转换器进行后续转换
		HashMap hm = new HashMap();
		hm.put("cfx", cfxMap);
		muleMessage.setPayload(hm);

	}

}
