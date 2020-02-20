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
	 * ���ղ���ϵͳ��9005�������ӱ��ģ�ת����Tips
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		// HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������ͷ headMap
		String recvnode = (String) headMap.get("DES");// ���ջ�������
		String sendnode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��

		String _srecvno = null;
		String _ssendno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
			throw new ITFEBizException("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
		}

		// �ǽ�����־
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
					logger.error("=====��Ӧ��������δ��sysconfig.xml��TBS_TREANDBANK������=====");
					return;
				}
			}else{
				logger.error("=====��Ӧ��������δ��sysconfig.xml��TBS_TREANDBANK������=====");
				return;
			}
			
			MuleClient client = new MuleClient();

			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,"TBS_3001_OUT");
			message.setProperty(MessagePropertyKeys.MSG_STATE, DealCodeConstants.DEALCODE_ITFE_RECEIVER);
			message.setProperty(MessagePropertyKeys.MSG_ADDWORD, "������");
			message.setProperty(MessagePropertyKeys.MSG_TAG_KEY, msgNo);
			message.setProperty(MessagePropertyKeys.MSG_DESC, sendnode);
			message.setProperty(MessagePropertyKeys.MSG_REF, msgid);
			//���շ�������ֵ
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, _srecvno);	//����
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, _ssendno);	//����
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			message.setProperty(MessagePropertyKeys.MSG_PACK_NO, msgid.substring(msgid.length()-8)); //�����д��
			
			message.setPayload(map);

			message = client.send("vm://ManagerMsgWithCommBank", message);

		} catch (MuleException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		} 
	}
}
