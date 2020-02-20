package com.cfcc.itfe.msgmanager.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;

public class Recv3000TbsMsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3000TbsMsgServer.class);

	/**
	 * 接收财政系统的9005测试连接报文，转发给Tips
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		// HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// 解析报文头 headMap
		String recvnode = (String) headMap.get("DES");// 接收机构代码
		String sendnode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号

		String _srecvno = null;
		String _ssendno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}

		// 记接收日志
		MsgLogFacade.writeRcvLog(_srecvno, _ssendno, (String) headMap
				.get("SRC"), TimeFacade.getCurrentStringTime(),
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				(String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH"), 0, null, null, null, null, null,
				null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, (String) headMap
						.get("MsgNo"));

		try {
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSpaybankcode(sendnode);
			HashMap<String, String> hm= ITFECommonConstant.TBS_TREANDBANK;
			Set<String> setStr = ITFECommonConstant.TBS_TREANDBANK.keySet();
			if(setStr!=null && setStr.size()>0){
				for(String tmpStr:setStr){
					if(hm.get(tmpStr).equals(sendnode.substring(0, 3))){
						vDto.setStrecode(tmpStr);
						break;
					}
				}
				if(vDto.getStrecode()==null){
					logger.error("=====对应的清算行未在sysconfig.xml的TBS_TREANDBANK中配置=====");
					return;
				}
			}else{
				logger.error("=====对应的清算行未在sysconfig.xml的TBS_TREANDBANK中配置=====");
				return;
			}
			
			MuleClient client = new MuleClient();

			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,"TBS_3001_OUT");
			message.setProperty(MessagePropertyKeys.MSG_STATE, DealCodeConstants.DEALCODE_ITFE_RECEIVER);
			message.setProperty(MessagePropertyKeys.MSG_ADDWORD, "已收妥");
			message.setProperty(MessagePropertyKeys.MSG_TAG_KEY, msgNo);
			message.setProperty(MessagePropertyKeys.MSG_DESC, sendnode);
			message.setProperty(MessagePropertyKeys.MSG_REF, msgid);
			//接收发送主键值
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, _srecvno);	//接收
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, _ssendno);	//发送
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			message.setProperty(MessagePropertyKeys.MSG_PACK_NO, msgid.substring(msgid.length()-8)); //随便填写的
			
			message.setPayload(map);

			message = client.send("vm://ManagerMsgWithCommBank", message);

		} catch (MuleException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		} 
	}
}
