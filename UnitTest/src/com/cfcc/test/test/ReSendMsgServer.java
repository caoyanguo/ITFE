package com.cfcc.test.test;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class ReSendMsgServer {
	private static Log logger = LogFactory.getLog(ReSendMsgServer.class);
	
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}
	
	public void dealMsg(String eventContext) throws ITFEBizException {
		try {
//			String message
//			TvSendlogDto _dto = (TvSendlogDto) message.getPayload();
			TvSendlogDto dt = new TvSendlogDto();
			dt.setSpackno("00009877");
//			List l = CommonFacade.getODB().findRsByDtoWithUR(dt);
			String sql = "select * from TV_SENDLOG where s_packno='00009877'";
			SQLExecutor sqlExec;
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			SQLResults res = sqlExec.runQueryCloseCon(sql,TvSendlogDto.class);
			List l = (List) res.getDtoCollection();
//			List l  = DatabaseFacade.getODB().findWithUR(TvSendlogDto.class, " s_packno='00009877'");
			TvSendlogDto _dto = (TvSendlogDto)l.get(0);
			File msgxmlFile = new File(_dto.getStitle());
			String msg = FileUtils.readFileToString(msgxmlFile,"GB18030");
			//在进行处理之前首先将文件的CA签名去掉
			FileUtil.getInstance().removeCAOfXML(_dto.getStitle(), msg, "</CFX>");
			StringBuffer newMsg = new StringBuffer();
			// 将以前的发送日志作废掉
			_dto.setSretcode(DealCodeConstants.DEALCODE_ITFE_CANCELLATION);
			_dto.setSretcodedesc("重发时作废");
			_dto.setSproctime(new Timestamp(new java.util.Date().getTime()));
			DatabaseFacade.getDb().update(_dto);

			// 将发送日志改为已发送，处理时间设为null，处理码说明设为null，重新生成流水号，从而记发送日志
			_dto.setSretcode(DealCodeConstants.DEALCODE_ITFE_SEND);// 处理码
			_dto.setSretcodedesc(null);
			_dto.setSproctime(null);
			_dto.setSsendno(StampFacade.getStampSendSeq("FS"));// 流水号
			_dto.setSsendtime(new Timestamp(new java.util.Date().getTime()));// 发送时间
			// 向财政段发送的，不需要修改Msgid
			if (MsgLogFacade.resendtofin(_dto.getSifsend(), _dto
					.getSoperationtypecode())) {
				newMsg.append(msg);
				DatabaseFacade.getDb().create(_dto);// 保存发送日志
				// 向Tips发送的，需要修改Msgid
			} else {
				String msgid = MsgSeqFacade.getMsgSendSeq();
				String tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
						.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				// 更新报文文件报文头信息
				int msgidBegin = msg.indexOf("<MsgID>");
				int msgrefEnd = msg.indexOf("</MsgRef>") + 9;
				newMsg.append(msg.substring(0, msgidBegin));
				newMsg.append("<MsgID>" + msgid + "</MsgID>");
				newMsg.append("\n\r");
				newMsg.append("<MsgRef>" + msgid + "</MsgRef>");
				newMsg.append(msg.substring(msgrefEnd, msg.length()));
				int packNoBegin = msg.indexOf("<PackNo>");
				int packNoEnd = msg.indexOf("</PackNo>") + 9;
				//如果存在包流水号的话需要修改 包流水号
				if (packNoBegin >0) {
					newMsg.append(msg.substring(0, msgidBegin));
				    newMsg.append("<PackNo>" + tmpPackNo + "<PackNo>");
					newMsg.append(msg.substring(packNoEnd, msg.length()));
					//同时更新明细报文中得包流水号
					HashMap<String, String> bizMsgNoMap = ITFECommonConstant.bizMsgNoList;
					// 如果是业务报文需要更新文件与包的对应关系和业务表明细的状态
					if (bizMsgNoMap.containsKey(_dto
							.getSoperationtypecode())) {
						// 根据条件更新文件与包的对应关系表
						MsgRecvFacade.updateFileRefPackNo( _dto.getSpackno(), tmpPackNo);
						// 更新业务表中的包流水号
							MsgRecvFacade.updateNewPackNoByOldPackNo(_dto
									.getSoperationtypecode(), _dto.getSpackno(), tmpPackNo, bizMsgNoMap);
						
					}
				}
				_dto.setStitle("");
				_dto.setSseq(msgid);
				_dto.setSpackno(tmpPackNo);
//				message.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
//						DatabaseFacade.getDb().createWithResult(_dto));
			}
//			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, _dto
//					.getSoperationtypecode());
			//重发判断新旧接口
			if(ITFECommonConstant.IFNEWINTERFACE.equals("1")&&("1104".equals( _dto.getSoperationtypecode()) || "1105".equals( _dto.getSoperationtypecode()))){
//				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, _dto
//						.getSoperationtypecode()+"2");
			}
			
//			message.setPayload(newMsg.toString());
		} catch (Exception e) {
			logger.error("报文重发出现异常", e);
			throw new ITFEBizException("报文重发出现异常", e);
		}
	}
	
	public static void main(String[] args) {
		ReSendMsgServer server = new ReSendMsgServer();
		try {
			server.dealMsg("");
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
