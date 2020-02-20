/**
 * ��������˰Ʊ����
 */
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
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor5003;

/**
 * @author wangtuo
 * 
 *         comments����������˰Ʊ����
 */
public class Send5003MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send5003MsgServer.class);

	/**
	 * ������Ϣ����
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		HeadDto headdto = (HeadDto) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_HEAD_DTO);

		String sendorgcode = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_BILL_CODE);

		String applydate = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_DATE);

		String sorgcode = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ORGCODE);
		// �����Բ��Ա���
		Map xmlMap = Dto2MapFor5003.tranfor(headdto, sendorgcode, applydate,sorgcode);
		// ������Ϣ��
		muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY, headdto
				.get_msgNo());

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
						sorgcode,
						ITFECommonConstant.DES_NODE, applydate, headdto
								.get_msgNo(), null, 0, null, null, null, null,
						sendorgcode, null, headdto.get_msgID(),
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), MsgConstant.ITFE_SEND, null));
		muleMessage.setPayload(xmlMap);
	}
}
