package com.cfcc.itfe.service.sendbiz.taxorgsosecry;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
/**
 * @author db2admin
 * @time   13-01-10 10:23:36
 * codecomment: 
 */

public class TaxOrgSoSecryService extends AbstractTaxOrgSoSecryService {
	private static Log log = LogFactory.getLog(TaxOrgSoSecryService.class);	


	/**
	 * ���ͱ���
	 	 
	 * @generated
	 * @param sendOrgCode
	 * @param inTreDate
	 * @param msgno
	 * @throws ITFEBizException	 
	 */
    public void sendMsg(String sendOrgCode, String inTreDate, String msgno) throws ITFEBizException {
    	HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC(sendOrgCode);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_DES(inTreDate);
		headdto.set_msgNo(msgno);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
		} catch (SequenceException e) {
			log.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		headdto.set_workDate(TimeFacade.getCurrentStringTime());

		try {
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgno + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sendOrgCode);// ���ͻ��ش���
			message.setProperty(MessagePropertyKeys.MSG_DATE, inTreDate);// �������
			message.setPayload(map);
			if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			log.error("���ú�̨���Ĵ����ʱ������쳣!", e);
			throw new ITFEBizException("���ú�̨���Ĵ����ʱ������쳣!", e);
		}
    }

}