package com.cfcc.itfe.msgmanager.msg;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor9006;

public class LoginTipsServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(LoginTipsServer.class);
	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		HeadDto headdto = (HeadDto) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_HEAD_DTO);
		ContentDto contentdto = (ContentDto) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_CONTENT);
		String orgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE); // ��������

		// ��½Tips����
		Map xmlMap = Dto2MapFor9006.tranfor(headdto,contentdto);
		// ������Ϣ��
		eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_NO_KEY,
				MsgConstant.MSG_NO_9006);
		eventContext.getMessage().setPayload(xmlMap);
		
		String _ssendno = null;
		try {
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
			throw new ITFEBizException("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
		}
		
		// д������־
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(_ssendno, null,
						orgcode,
						ITFECommonConstant.DES_NODE, TimeFacade.getCurrentStringTime(), headdto
								.get_msgNo(), null, 0, null, null, null, null,
						null, null, headdto.get_msgID(),
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, null));
		muleMessage.setPayload(xmlMap);
       
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
