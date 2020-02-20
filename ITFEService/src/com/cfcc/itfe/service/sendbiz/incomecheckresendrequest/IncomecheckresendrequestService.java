package com.cfcc.itfe.service.sendbiz.incomecheckresendrequest;

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
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
/**
 * @author t60
 * @time   12-02-24 10:44:42
 * codecomment: 
 */

public class IncomecheckresendrequestService extends AbstractIncomecheckresendrequestService {
	private static Log log = LogFactory
	.getLog(IncomecheckresendrequestService.class);

	public void sendMsg(TvSendlogDto dto) throws ITFEBizException {
		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC(ITFECommonConstant.SRC_NODE);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_DES(ITFECommonConstant.DES_NODE);
		headdto.set_msgNo(MsgConstant.MSG_NO_9113);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
		} catch (SequenceException e) {
			log.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		String sdate = TimeFacade.getCurrentStringTime();
		headdto.set_workDate(sdate);

		try {
			MuleClient client = new MuleClient();
			MuleMessage message = new DefaultMuleMessage(dto);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_NO_9113+"_OUT");
//			dto.setSsendorgcode(this.getLoginInfo().getSorgcode());//�����������
//			dto.setSdate(sdate);//ί������
			message.setProperty(MessagePropertyKeys.MSG_DTO, dto);
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO,headdto);
			message.setPayload(dto);
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