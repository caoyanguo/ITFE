package com.cfcc.itfe.service.sendbiz.trastatuscheck;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author db2admin
 * @time   12-03-12 15:15:32
 * codecomment: 
 */

public class TraStatusCheckService extends AbstractTraStatusCheckService {
	private static Log log = LogFactory.getLog(TraStatusCheckService.class);	


	/**
	 * 交易状态查询请求(9003)
	 	 
	 * @generated
	 * @param SendOrgCode
	 * @param SearchType
	 * @param OriMsgNo
	 * @param OriEntrustDate
	 * @param OriPackNo
	 * @param OriTraNo
	 * @throws ITFEBizException	 
	 */
    public void traStatusCheck(String sendOrgCode, String searchType, String oriMsgNo, String oriEntrustDate, String oriPackNo, String oriTraNo) throws ITFEBizException {
    	HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

//		sendOrgCode = this.getLoginInfo().getSorgcode();
		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC(ITFECommonConstant.SRC_NODE);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_DES(ITFECommonConstant.DES_NODE);
		headdto.set_msgNo(MsgConstant.MSG_NO_9003);
		
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// 报文头 HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_9003);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			log.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		HashMap<String, Object> TraStatusCheck9003 = new HashMap<String, Object>();
		msgMap.put("TraStatusCheck9003", TraStatusCheck9003);
		TraStatusCheck9003.put("SendOrgCode", sendOrgCode);
		TraStatusCheck9003.put("SearchType", searchType);
		TraStatusCheck9003.put("OriMsgNo", oriMsgNo);
		TraStatusCheck9003.put("OriEntrustDate", oriEntrustDate);
		TraStatusCheck9003.put("OriPackNo", oriPackNo);
		TraStatusCheck9003.put("OriTraNo", oriTraNo);
		
		try {
			MuleClient client = new MuleClient();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_9003+"_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sendOrgCode);// 发送机关代码
			message.setProperty(MessagePropertyKeys.MSG_DATE, TimeFacade.getCurrentStringTime());// 入库日期
			message.setPayload(map);
			if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			log.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}
    }

	/**
	 * 查看查询结果
	 	 
	 * @generated
	 * @param SendOrgCode
	 * @param SearchType
	 * @param OriMsgNo
	 * @param OriEntrustDate
	 * @param OriPackNo
	 * @param OriTraNo
	 * @throws ITFEBizException	 
	 */
    public List viewSelectRusult(String sendOrgCode, String searchType,  String oriMsgNo, String oriEntrustDate, String oriPackNo, String oriTraNo) throws ITFEBizException {
    		TvRecvlogDto recvlogdto  = new TvRecvlogDto();
//    		recvlogdto.setSrecvorgcode(this.getLoginInfo().getSorgcode());
    		recvlogdto.setSrecvorgcode(sendOrgCode);
    		recvlogdto.setSturnsendflag(searchType);
    		recvlogdto.setSusercode(oriMsgNo);
    		recvlogdto.setSdate(oriEntrustDate);
    		recvlogdto.setSoperationtypecode("9004");
    		if(oriPackNo!=null&&!oriPackNo.equals(""))
    			recvlogdto.setSpackno(oriPackNo);
    		if(oriTraNo!=null&&!oriTraNo.equals(""))
    			recvlogdto.setSbatch(oriTraNo);
    		List returnList = null;
    		try {
    			returnList = CommonFacade.getODB().findRsByDto(recvlogdto);
			}catch (JAFDatabaseException e) {
				log.error("根据9003条件查找发送日志表出现异常!", e);
				throw new ITFEBizException("根据9003条件查找发送日志表出现异常!", e);
			} catch (ValidateException e) {
				log.error("根据9003条件查找发送日志表出现异常!", e);
				throw new ITFEBizException("根据9003条件查找发送日志表出现异常!", e);
			}
			return returnList;
    }

}