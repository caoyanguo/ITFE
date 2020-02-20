/**
 * 
 */
package com.cfcc.itfe.msgmanager.msg;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor3001Tbs;

/**
 * @author wangtuo
 * 
 */
public class Send3001TbsMsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send3001TbsMsgServer.class);

	/**
	 * ������Ϣ����
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		String msgid = null;
		try {
			msgid = MsgSeqFacade.getMsgSendSeq();
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		// ���Ĵ�����
		String result = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_STATE);
		// ���Ĵ�����
		String addword = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ADDWORD);
		// ���ı��
		String msgno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_TAG_KEY);
		// ��������
		String desc = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DESC); //�Է������������
		// ���Ĳο���
		String msgref = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_REF);
		// ͨ�û�ִ����
		Map xmlMap = Dto2MapFor3001Tbs.tranfor(msgid, msgref, desc, result, addword, msgno);
		// ������Ϣ��
		muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_TBS_NO_3001 + "_VOUCHER_TBS");

		String _ssendno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_BILL_CODE);
		String _srecvno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE);
		TvVoucherinfoDto vDto = (TvVoucherinfoDto)eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DTO);//�����dto��ֻ�й��������ֵ
		String spackno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_PACK_NO); //����ˮ��(�����ģ�Ϊ�����ط�)
		
		// д������־
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(_ssendno, _srecvno,
						vDto.getStrecode()+"04",
						desc, TimeFacade.getCurrentStringTime(),
						MsgConstant.MSG_TBS_NO_3001, (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 1, null, spackno, vDto.getStrecode(),
						null, "", null, msgid,
						DealCodeConstants.DEALCODE_ITFE_SEND, msgno, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, "������"));
		muleMessage.setPayload(xmlMap);
	}
}
