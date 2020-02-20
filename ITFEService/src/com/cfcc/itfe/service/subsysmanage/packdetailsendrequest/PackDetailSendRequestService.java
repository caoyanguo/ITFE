package com.cfcc.itfe.service.subsysmanage.packdetailsendrequest;

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
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor9111;

/**
 * @author caoyg
 * @time 09-11-30 11:02:47 codecomment:
 */

public class PackDetailSendRequestService extends AbstractPackDetailSendRequestService {

	private static Log logger = LogFactory.getLog(PackDetailSendRequestService.class);

	public String sendRequestMsg(ContentDto dto) throws ITFEBizException {
		String sorgcode = this.getLoginInfo().getSorgcode(); // ȡ�û�������
		try {
			MuleClient client = new MuleClient();

			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_9111);
			message.setProperty(MessagePropertyKeys.MSG_DTO, dto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sorgcode);
			if(sorgcode!=null&&sorgcode.startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			logger.error("���ú�̨���Ĵ����ʱ������쳣!", e);
			throw new ITFEBizException("���ú�̨���Ĵ����ʱ������쳣!", e);
		}

		return null;
	}

}