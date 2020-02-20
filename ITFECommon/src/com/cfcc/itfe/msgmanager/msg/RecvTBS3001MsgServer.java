package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class RecvTBS3001MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(RecvTBS3001MsgServer.class);

	/**
	 * 接收代理行发送的通用回执报文(对应原发送的资金报文1000)
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			String msgRef = (String) headMap.get("MsgRef");
			String msgid = (String) headMap.get("MsgID");
			HashMap returnmap = (HashMap) msgMap.get("MsgReturn3001");
			String orimsgno = (String) returnmap.get("OriMsgNo");
			String Result = (String) returnmap.get("Result");
			String AddWord = (String) returnmap.get("AddWord");
			String Information = (String) returnmap.get("Information");
			if(Information !=null && Information.length()>200){
				Information = Information.substring(0, 200);
			}
			String recvorg = (String) headMap.get("DES");
			String sendno = null;// 发送流水号
			String sbillorg = null;// 出票单位
			String packno = null;// 原包号
			String strecode = null;// 国库代码
			BigDecimal nmoney = null; //原金额
			
			String sdemo = "原报文编号：" + orimsgno;
			String filepath = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
			String ifsend = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER);

			// 凭证处理码
			String state = PublicSearchFacade.getDetailStateByDealCode(Result);
			// 日志处理吗
			String pkgstate = PublicSearchFacade.getPackageStateByDealCode(Result);
			// 接收日志流水d
			String _srecvno = StampFacade.getStampSendSeq("JS");
			// 查找原发送日志
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,orimsgno);
			if(null != senddto){
				// 更新原发送日志流水号
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto, pkgstate, _srecvno,AddWord);
				strecode = senddto.getStrecode();
				sendno = senddto.getSsendno();
				recvorg = senddto.getSsendorgcode();//机构代码
				nmoney = senddto.getNmoney();
				packno = senddto.getSpackno();
			}
			
			// 记接收日志
			MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, (String)headMap.get("WorkDate"), (String)headMap.get("MsgNo"),
					(String)headMap.get("SRC"), filepath, 1, nmoney, packno, strecode, null,
					sbillorg, null, msgid,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, Information, null,
					ifsend, "0", sdemo + "原包流水号:" + packno);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}
	}
}
